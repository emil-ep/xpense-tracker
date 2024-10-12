package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.entities.Statements;

import java.io.File;
import java.util.List;

public interface StatementService {

    void saveStatement(Statements statements);

    List<String> extractHeaderMapper(File file);
}
