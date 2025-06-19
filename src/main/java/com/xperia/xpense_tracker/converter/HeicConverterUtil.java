package com.xperia.xpense_tracker.converter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HeicConverterUtil {

    /**
     * Returns true if the given tool is on the PATH and responds (e.g. "magick -version").
     */
    public static boolean isToolAvailable(String command, String versionArg) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command, versionArg);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            // wait up to 3s for it to return
            if (!p.waitFor(3, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return false;
            }
            return p.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Returns true if ImageMagick's `magick` CLI is available on the PATH.
     */
    public static boolean useImageMagick() {
        return isToolAvailable("magick", "-version");
    }
}
