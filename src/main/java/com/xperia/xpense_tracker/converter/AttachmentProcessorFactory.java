package com.xperia.xpense_tracker.converter;

public class AttachmentProcessorFactory<T, R> {

    public static AbstractAttachmentProcessor findImageProcessor(String fileExtension){

        switch (fileExtension){
            case "heic" -> {
                return new HEICProcessor();
            }
            case "pdf" -> {
                return new PdfProcessor();
            }
            case null, default -> {
                return new DefaultImageProcessor();
            }
        }
    }
}
