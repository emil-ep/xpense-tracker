package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.tabula.*;
import technology.tabula.detectors.SpreadsheetDetectionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PdfProcessor extends FileProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfProcessor.class);

    @Override
    public List<HashMap<Integer, String>> parseFile(File file) throws TrackerBadRequestException {
        return List.of();
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
            while(iterator.hasNext() && pageCount == 1){
                Page page = iterator.next();
                List<Rectangle> tableAreas = tableExtractor.detect(page);
                for (Rectangle tableArea: tableAreas){
                    Page subPage = page.getArea(tableArea);
                    List<Table> tables = spreadsheetExtractor.extract(subPage);
                    for (Table table: tables){
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
            LOGGER.error("Unable to load file as a PDDocument : {}", ex.getMessage());
        }

        return headerValues;
    }
}
