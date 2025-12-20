package com.xperia.xpense_tracker.converter;

public class ImageProcessorFactory<T, R> {

    public static AbstractAttachmentProcessor findImageProcessor(String fileExtension){

        switch (fileExtension){
            case "heic" -> {
                return new HEICProcessor();
            }
            case null, default -> {
                return new DefaultImageProcessor();
            }
        }
    }
}
