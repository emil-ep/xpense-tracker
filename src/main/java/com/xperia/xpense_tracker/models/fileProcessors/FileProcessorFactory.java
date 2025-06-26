package com.xperia.xpense_tracker.models.fileProcessors;

public class FileProcessorFactory {

    /**
     * Factory that create file processor based on the file extension name
     * @param fileExtension represents the file extension. Currently, we support xlsx, delimited, csv and pdf formats
     * @return the actual file processor instance
     */
    public static FileProcessor createFileProcessor(String fileExtension){
        switch (fileExtension){
            case "xlsx" -> {
                return new ExcelProcessor();
            }
            case "delimited", "csv", "txt" -> {
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
