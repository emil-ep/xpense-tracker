package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Statements;
import com.xperia.xpense_tracker.models.fileProcessors.FileHeader;

import java.io.File;
import java.util.List;

public interface StatementService {

    void saveStatement(Statements statements);

    FileHeader extractHeaderMapper(File file);

    List<Statements> listAll();
}
