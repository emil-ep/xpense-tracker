package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExcelProcessor extends FileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelProcessor.class);

    public ExcelProcessor(Map<Integer, String> headerIndexMap){
        this.headerIndexMap = headerIndexMap;
    }

    public ExcelProcessor(){

    }

    private Object getValueFromCell(Cell cell){
        switch (cell.getCellType()){
            case STRING -> {
                return cell.getStringCellValue();
            }
            case _NONE, FORMULA, BLANK, ERROR -> {
                return null;
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
        }
        return null;
    }

    @Override
    public List<HashMap<Integer, String>> parseFile(File file) throws TrackerBadRequestException {

        try{
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row topRow = sheet.getRow(0);
            sheet.removeRow(topRow);
            Iterator<Row> rowIterator = sheet.rowIterator();
            List<HashMap<Integer, String>> bookMapList = new ArrayList<>();
            while(rowIterator.hasNext()){
                Row currentRow = rowIterator.next();
                Iterator<Cell> cellIterator = currentRow.cellIterator();
                HashMap<Integer, String> cellValueMap = new HashMap<>();
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    Object value = getValueFromCell(cell);
                    cellValueMap.put(cell.getColumnIndex(), value != null ? String.valueOf(value) : null);
                }
                bookMapList.add(cellValueMap);
            }
            return bookMapList;
        }catch (IOException | InvalidFormatException e){
            LOGGER.error("The file requested for parsing faced error, {}", e.getMessage());
            throw new TrackerBadRequestException("The file requested is not a valid excel file");
        }
    }

    @Override
    public List<String> fetchHeaders(File file) throws TrackerException {
        try {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Iterator<Cell> cellIterator = headerRow.cellIterator();
            List<String> headerValues = new LinkedList<>();
            while (cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                String value = String.valueOf(getValueFromCell(cell));
                headerValues.add(value);
            }
            return headerValues;
        }catch (Exception ex){
            LOGGER.error("Unable to fetch headers from the file : {}", ex.getMessage());
            throw new TrackerException("Unable to fetch headers from the file");
        }
    }
}
