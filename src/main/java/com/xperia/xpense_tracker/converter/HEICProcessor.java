package com.xperia.xpense_tracker.converter;
import com.xperia.xpense_tracker.exception.customexception.TrackerUnknownException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


public class HEICProcessor extends AbstractImageProcessor<MultipartFile, BufferedImage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HEICProcessor.class);

    private FFmpegFrameGrabber grabber;

    @Override
    BufferedImage convertImage(MultipartFile heicImage) {

        File tempFile;
        try{
            tempFile = File.createTempFile("temp", ".heic");
            try(InputStream in = heicImage.getInputStream();
                OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            grabber = new FFmpegFrameGrabber(tempFile);
            grabber.setFormat("image2");
            grabber.start();
            Frame frame = grabber.grabImage();
            if (frame == null) {
                throw new IOException("No image found in the file: " + heicImage.getName());
            }
            Java2DFrameConverter converter = new Java2DFrameConverter();
            return converter.convert(frame);

        }catch (Exception ex){
            LOGGER.error("Error while converting image : {} ", ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void saveImage(MultipartFile heicImage, String path, String fileName) {
        try{
            BufferedImage bufferedImage = convertImage(heicImage);
            if (null == bufferedImage){
                throw new TrackerUnknownException("Received null for converted image ");
            }
            if (!ImageIO.write(bufferedImage, "png", new File(path))) {
                throw new IOException("Failed to write PNG image");
            }
            grabber.stop();
        }catch (IOException ex){
            LOGGER.error("Error saving Image : {}", ex.getMessage(), ex);
        }
    }
}
