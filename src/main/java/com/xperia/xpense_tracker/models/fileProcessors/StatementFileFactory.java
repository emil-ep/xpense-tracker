package com.xperia.xpense_tracker.models.fileProcessors;

public class StatementFileFactory {


    public static StatementFile createStatementFile(String fileExtension){
        switch (fileExtension){
            case "xlsx" -> {
                return new ExcelStatement();
            }

            default -> {
                return new ExcelStatement();
            }
        }
    }
}
