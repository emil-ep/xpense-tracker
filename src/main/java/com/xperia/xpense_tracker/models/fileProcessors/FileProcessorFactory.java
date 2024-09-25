package com.xperia.xpense_tracker.models.fileProcessors;

public class FileProcessorFactory {


    public static FileProcessor createFileProcessor(String fileExtension){
        switch (fileExtension){
            case "xlsx" -> {
                return new ExcelProcessor();
            }

            default -> {
                return null;
            }
        }
    }
}
