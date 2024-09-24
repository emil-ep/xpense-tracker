package com.xperia.xpense_tracker.models.fileProcessors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExcelStatement extends StatementFile{

    public ExcelStatement(Map<Integer, String> headerIndexMap){
        this.headerIndexMap = headerIndexMap;
    }

    public ExcelStatement(){

    }

    private Object getValueFromCell(Cell cell){
        switch (cell.getCellType()){
            case STRING -> {
                return cell.getStringCellValue();
            }
            case _NONE -> {
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)){
                    return cell.getDateCellValue();
                }else{
                    return cell.getNumericCellValue();
                }
            }
            case BOOLEAN ->  {
                return cell.getBooleanCellValue();
            }
            case FORMULA -> {
            }
            case BLANK -> {
                return "";
            }

            case ERROR -> {
            }
        }
        return null;
    }

    @Override
    public List<HashMap<Integer, Object>> parseExpenseFromFile(Map<Integer, String> headerIndexMap, File file) {

        try{
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row topRow = sheet.getRow(0);
            sheet.removeRow(topRow);
            Iterator<Row> rowIterator = sheet.rowIterator();
            List<HashMap<Integer, Object>> bookMapList = new ArrayList<>();
            while(rowIterator.hasNext()){
                Row currentRow = rowIterator.next();
                Iterator<Cell> cellIterator = currentRow.cellIterator();
                HashMap<Integer, Object> cellValueMap = new HashMap<>();
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    cellValueMap.put(cell.getColumnIndex(), getValueFromCell(cell));
                    bookMapList.add(cellValueMap);
                }
            }
            return bookMapList;
        }catch (IOException e){
            System.out.println("IOException occurred");
        }catch (InvalidFormatException ex){
            System.out.println("InvalidFormatException occurred");
        }

        return List.of();
    }
}
