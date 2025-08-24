package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.exception.customexception.TrackerUnknownException;
import com.xperia.xpense_tracker.models.entities.ExpenseFields;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelProcessor extends FileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelProcessor.class);

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

    /**
     * Apache POI deals with formats differently. xls formats is from 2003-2007
     * xlxs format is 2007 +
     * @param file The file in question
     * @return the appropriate workbook
     */
    private Workbook findRelevantWorkBook(File file){
        try{
            String extension = file.getName().split("\\.")[1].toLowerCase();
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook;
            if (extension.equalsIgnoreCase("xls")){
                workbook = new HSSFWorkbook(fis);
            }else{
                workbook = new XSSFWorkbook(fis);
            }

            return workbook;
        } catch (IOException ex){
            LOGGER.error("The file requested for parsing faced error, {}", ex.getMessage(), ex);
            throw new TrackerBadRequestException("The file requested is not a valid excel file");
        }
    }

    @Override
    public List<HashMap<Integer, String>> parseFile(File file, Integer headerStartIndex) throws TrackerBadRequestException {

        Workbook workbook = findRelevantWorkBook(file);
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i <= headerStartIndex; i++){
            Row row = sheet.getRow(i);
            sheet.removeRow(row);
        }
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
    }

    @Override
    public FileHeader fetchHeaders(File file) throws TrackerException {
        try {
            Workbook workbook = findRelevantWorkBook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();
            int rowCount = 0;
            while(rowIterator.hasNext()){
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<String> headerValues = new LinkedList<>();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    String value = String.valueOf(getValueFromCell(cell));
                    headerValues.add(value);
                }
                List<String> matchedValues = headerValues.stream()
                        .filter(header -> ExpenseFields.findMatchingField(header.trim()) != null)
                        .toList();
                if (matchedValues.size() > HEADER_MATCH_THRESHOLD){
                    return new FileHeader(rowCount, headerValues);
                }
                rowCount ++;
            }
            throw new TrackerBadRequestException("Required headers not found in the statement uploaded");
        }catch (Exception ex){
            LOGGER.error("Unable to fetch headers from the file : {}", ex.getMessage(), ex);
            throw new TrackerUnknownException("Unable to fetch headers from the file");
        }
    }
}
