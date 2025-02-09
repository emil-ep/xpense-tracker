package com.xperia.xpense_tracker.models.fileProcessors;

public class FileProcessorFactory {


    public static FileProcessor createFileProcessor(String fileExtension){
        switch (fileExtension){
            case "xlsx" -> {
                return new ExcelProcessor();
            }
            case "delimited", "csv" -> {
                return new DelimitedProcessor();
            }
            case "pdf" -> {
                return new PdfProcessor();
            }
            default -> {
                return null;
            }
        }
    }
}
