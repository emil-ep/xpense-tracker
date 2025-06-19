package com.xperia.xpense_tracker.converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;


public class HEICProcessor extends AbstractImageProcessor<MultipartFile, InputStream> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HEICProcessor.class);

    @Override
    InputStream convertImage(MultipartFile heicImage, String path, String fileName) {

        try{
            Path tempHeic = Files.createTempFile(heicImage.getName() + "_", ".heic");
            File heicFile = tempHeic.toFile();
            Path tempPng = Files.createTempFile("conv_" + fileName, ".png");
            try (InputStream in = new BufferedInputStream(heicImage.getInputStream());
                 OutputStream out = new FileOutputStream(heicFile)) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = in.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }
            }
            String tempFileName = path + "/temp_" + fileName;
            ProcessBuilder pb = HeicConverterUtil.useImageMagick()
                    ? new ProcessBuilder("magick", heicFile.getAbsolutePath(), tempPng.toString())
                    : new ProcessBuilder("ffmpeg", "-y", "-i",
                    heicFile.getAbsolutePath(), tempPng.toString());

            pb.redirectErrorStream(true);
            Process proc = pb.start();

            if (!proc.waitFor(30, TimeUnit.SECONDS) || proc.exitValue() != 0) {
                // read any error output for diagnostics
                try (BufferedReader err = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    StringBuilder log = new StringBuilder();
                    while ((line = err.readLine()) != null) log.append(line).append('\n');
                    throw new IOException("Conversion failed (exit " + proc.exitValue() + "):\n" + log);
                } finally {
                    Files.deleteIfExists(tempHeic);
                    Files.deleteIfExists(tempPng);
                }
            }
            byte[] pngBytes = Files.readAllBytes(tempPng);
            Files.deleteIfExists(tempHeic);
            Files.deleteIfExists(tempPng);
            return new ByteArrayInputStream(pngBytes);

        }catch (Exception ex){
            LOGGER.error("Exception while converting file  :  {}", fileName, ex);
        }
        return null;
    }

    @Override
    public void saveImage(MultipartFile heicImage, String path, String fileName) {
        InputStream reader = convertImage(heicImage, path, fileName);
        if (reader == null){
            LOGGER.error("Save image failed for file : {}", fileName);
        }

        Path targetFile = Paths.get(path, fileName);
        try (InputStream in = reader) {
            // This will overwrite if the file already exists
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Saved file : {}", fileName);
        }catch (Exception ex){
            LOGGER.error("Error saving file : {}", fileName, ex);
        }
    }
}
