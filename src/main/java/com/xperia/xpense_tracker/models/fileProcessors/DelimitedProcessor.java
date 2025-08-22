package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.exception.customexception.TrackerUnknownException;
import com.xperia.xpense_tracker.models.entities.ExpenseFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DelimitedProcessor extends FileProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(DelimitedProcessor.class);

    @Override
    public List<HashMap<Integer, String>> parseFile(File file) throws TrackerBadRequestException {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
            String line;
            List<HashMap<Integer, String>> dataList= new ArrayList<>();
            while((line = reader.readLine()) != null){
                if (line.isEmpty()) continue;
                String[] columns = line.split(",");
                if (columns.length > 0){
                    HashMap<Integer, String> lineData = new HashMap<>();
                    for (int i = 0; i < columns.length; i++){
                        lineData.put(i, columns[i].trim());
                    }
                    dataList.add(lineData);
                }
            }
            //removing the headers from the data
            dataList.remove(0);
            return dataList;
        }catch (IOException  ex){
            LOGGER.error("The file requested for parsing faced error, {}", ex.getMessage(), ex);
            throw new TrackerBadRequestException("The file requested is not a valid excel file");
        }
    }

    /**
     * This function goes through the statement and matches each line with the header dictionary in ExpenseFields
     * If any line contains column names which matches with the dictionary, it is returned as system identified header
     * We are keeping a threshold of HEADER_MATCH_THRESHOLD, so atleast this number of column name should be matched to
     * identify as a valid header
     * @param file the statement file
     * @return
     * @throws TrackerException
     */
    @Override
    public FileHeader fetchHeaders(File file) throws TrackerException {
        String delimiter = ",";
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()))){
            String line;
            int rowCount = 0;
            while ((line = bufferedReader.readLine()) != null){
                if (line.isEmpty()) continue;
                String[] columns = line.split(delimiter);
                if (columns.length > 0){
                    List<String> matches  = Arrays.stream(columns)
                            .filter(column -> ExpenseFields.findMatchingField(column.trim()) != null)
                            .toList();
                    //checking if there is a match, else continue to next line
                    if (matches.size() > HEADER_MATCH_THRESHOLD){
                        return new FileHeader(rowCount, Arrays.stream(columns).map(String::trim).toList());
                    }
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
