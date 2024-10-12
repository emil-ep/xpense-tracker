package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;
import com.xperia.xpense_tracker.models.entities.Statements;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessor;
import com.xperia.xpense_tracker.models.fileProcessors.FileProcessorFactory;
import com.xperia.xpense_tracker.repository.StatementsRepository;
import com.xperia.xpense_tracker.services.StatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class StatementServiceImpl implements StatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementServiceImpl.class);

    @Autowired
    private StatementsRepository statementsRepository;

    @Override
    public void saveStatement(Statements statements) {
        if(statementsRepository.findByFileName(statements.getFileName()).isPresent()){
            LOGGER.error("File with the same name found : {}", statements.getFileName());
            throw new TrackerBadRequestException("File with the same name found!");
        }
        try{
            statementsRepository.save(statements);
        }catch (Exception ex){
            LOGGER.error("Exception occurred while saving statement : {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<String> extractHeaderMapper(File file) {
        String extension = file.getName().split("\\.")[1];
        FileProcessor fileProcessor = FileProcessorFactory.createFileProcessor(extension);
        if (fileProcessor == null){
            throw new TrackerBadRequestException("File format is invalid. Please upload xlsx files only");
        }
        try{
            return fileProcessor.fetchHeaders(file);
        }catch (TrackerException ex){
            LOGGER.error("unable to parse the file : {}", ex.getMessage());
            throw ex;
        }
    }
}
