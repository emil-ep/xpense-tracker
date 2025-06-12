package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.exception.customexception.TrackerUnknownException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.tabula.*;
import technology.tabula.detectors.SpreadsheetDetectionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Deprecated
public class PdfProcessor extends FileProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfProcessor.class);

    @Override
    public List<HashMap<Integer, String>> parseFile(File file) throws TrackerBadRequestException {
        PDDocument pdDocument;
        List<HashMap<Integer, String>> fileContents = new ArrayList<>();
        try{
            pdDocument = PDDocument.load(file);
            PageIterator iterator = new ObjectExtractor(pdDocument).extract();
            SpreadsheetDetectionAlgorithm tableExtractor = new SpreadsheetDetectionAlgorithm();
            SpreadsheetExtractionAlgorithm spreadsheetExtractor = new SpreadsheetExtractionAlgorithm();
            while(iterator.hasNext()){
                Page page = iterator.next();
                List<Rectangle> tableAreas = tableExtractor.detect(page);
                for (Rectangle tableArea: tableAreas){
                    Page subPage = page.getArea(tableArea);
                    List<Table> tables = spreadsheetExtractor.extract(subPage);
                    for (Table table: tables){
                        if (table.getRowCount() <= 1 || table.getColCount() <= 1){
                            continue;
                        }
                        List<HashMap<Integer, String>> pageContents = table.getRows().stream()
                                .map(row -> {
                                    HashMap<Integer, String> rowMap = new HashMap<>();
                                    for (int i = 0; i < row.size(); i++) {
                                        rowMap.put(i, row.get(i).getText().trim());
                                    }
                                    return rowMap;
                                }
                        ).toList();
                        fileContents.addAll(pageContents);
                    }
                }
            }
            pdDocument.close();
        }catch (IOException ex){
            LOGGER.error("Unable to load file as a PDDocument : {}", ex.getMessage(), ex);
            throw new TrackerUnknownException("Unable to load file : " + ex.getMessage());
        }catch (Exception ex){
            LOGGER.error("Exception occurred while parsing pdf file : {}", ex.getMessage(), ex);
            throw new TrackerUnknownException("Unable to fetch headers from the file");
        }
        if (!fileContents.isEmpty()){
            fileContents.removeFirst();
        }
        return fileContents;
    }

    @Override
    public List<String> fetchHeaders(File file) throws TrackerException {
        PDDocument pdDocument;
        List<String> headerValues = new LinkedList<>();
        int pageCount = 1;
        try{
            pdDocument = PDDocument.load(file);
            PageIterator iterator = new ObjectExtractor(pdDocument).extract();
            SpreadsheetDetectionAlgorithm tableExtractor = new SpreadsheetDetectionAlgorithm();
            SpreadsheetExtractionAlgorithm spreadsheetExtractor = new SpreadsheetExtractionAlgorithm();
            //we took a sample of HDFC statement and it contains the headers only for the first page
            // Assuming that's the case, we are only looking at first page to fetch the headers
            while(iterator.hasNext() && pageCount == 1){
                Page page = iterator.next();
                List<Rectangle> tableAreas = tableExtractor.detect(page);
                for (Rectangle tableArea: tableAreas){
                    Page subPage = page.getArea(tableArea);
                    List<Table> tables = spreadsheetExtractor.extract(subPage);
                    for (Table table: tables){
                        //ignoring tables with fewer columns -> chances are it isn't what we are looking for
                        if (table.getRowCount() <= 1 || table.getColCount() <= 1){
                            continue;
                        }
                        headerValues = table.getRows().stream()
                                .findFirst()
                                .map(row -> row.stream().map(cell -> cell.getText().trim()).toList())
                                .orElse(Collections.emptyList());
                    }
                }
                pageCount += 1;
            }
            pdDocument.close();
        }catch (IOException ex){
            LOGGER.error("Unable to load file as a PDDocument : {}", ex.getMessage(), ex);
            throw new TrackerUnknownException("Unable to load file : " + ex.getMessage());
        } catch (Exception ex){
            LOGGER.error("Exception occurred while parsing pdf file : {}", ex.getMessage(), ex);
            throw new TrackerUnknownException("Unable to fetch headers from the file");
        }

        return headerValues;
    }
}
