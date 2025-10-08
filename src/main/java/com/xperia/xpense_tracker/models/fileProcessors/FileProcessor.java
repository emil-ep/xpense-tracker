package com.xperia.xpense_tracker.models.fileProcessors;


import org.xperia.exception.TrackerBadRequestException;
import org.xperia.exception.TrackerException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public abstract class FileProcessor {

    protected static final int HEADER_MATCH_THRESHOLD = 3;

    public abstract List<HashMap<Integer, String>> parseFile(File file, Integer headerStartIndex) throws TrackerBadRequestException;

    public abstract FileHeader fetchHeaders(File file) throws TrackerException;

}
