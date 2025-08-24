package com.xperia.xpense_tracker.jobs;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.Statements;
import com.xperia.xpense_tracker.repository.StatementsRepository;
import com.xperia.xpense_tracker.services.ExpenseService;
import com.xperia.xpense_tracker.services.StatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("FileCleanupJob")
public class FileCleanupJob implements ScheduledJob{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileCleanupJob.class);

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Value("${file.upload.attachment.path}")
    private String attachmentUploadPath;

    @Autowired
    private StatementService statementService;

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private StatementsRepository statementsRepository;

    @Override
    public String getName() {
        return "FileCleanupJob";
    }

    @Override
    public void execute() {
        Path uploadPath = Paths.get(fileUploadPath);
        Path attachementPath = Paths.get(attachmentUploadPath);
        LOGGER.info("Starting FileCleanupJob ");
        try(Stream<Path> files = Files.list(uploadPath)){
            List<String> distinctStatementFileNames = expenseService.findDistinctStatementsOfExpenses()
                    .stream()
                    .map(Statements::getFileName)
                    .toList();

            List<Statements> notRequiredStatements = statementService.listAll()
                    .stream()
                    .filter(statement -> !distinctStatementFileNames.contains(statement.getFileName()))
                    .toList();
            LOGGER.info("Not required statements count : {}", notRequiredStatements.size());
            statementsRepository.deleteAll(notRequiredStatements);
            Set<Path> filesToRemove = files
                    .filter(file -> !file.toFile().isDirectory())
                    .filter(file -> !distinctStatementFileNames.contains(file.getFileName().toString()))
                    .collect(Collectors.toSet());
            LOGGER.info("Statement files To remove count : {}", filesToRemove.size());
            filesToRemove.forEach(toRemove -> {
                try{
                    Files.delete(toRemove);
                    LOGGER.debug("Removed the file : {}", toRemove.getFileName().toString());
                }catch (IOException ex){
                    LOGGER.error("Error while removing file : {}", toRemove.getFileName().toString(), ex);
                }
            });
        }catch (IOException ex){
            LOGGER.error("Error while opening path : {}", fileUploadPath);
        }

        try(Stream<Path> files = Files.list(attachementPath)){
            Set<String> attachmentFileNames = expenseService.listAll()
                    .stream()
                    .filter(expenses -> expenses.getAttachment() != null && !expenses.getAttachment().isEmpty())
                    .map(Expenses::getAttachment)
                    .collect(Collectors.toSet());
            Set<Path> filesToRemove = files
                    .filter(file -> !file.toFile().isDirectory())
                    .filter(file -> !attachmentFileNames.contains(file.getFileName().toString()))
                    .collect(Collectors.toSet());
            LOGGER.info("Attachment files To remove count : {}", filesToRemove.size());
            filesToRemove.forEach(toRemove -> {
                try{
                    Files.delete(toRemove);
                    LOGGER.debug("Removed the attachment : {}", toRemove.getFileName().toString());
                }catch (IOException ex){
                    LOGGER.error("Error while removing attachment : {}", toRemove.getFileName().toString(), ex);
                }
            });
            LOGGER.info("Completed FileCleanupJob");
        }catch (IOException ex){
            LOGGER.error("Error while opening path : {}", attachmentUploadPath);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
