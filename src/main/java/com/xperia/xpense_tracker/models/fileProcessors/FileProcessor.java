package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public abstract class FileProcessor {

    protected static final int HEADER_MATCH_THRESHOLD = 3;

    public abstract List<HashMap<Integer, String>> parseFile(File file) throws TrackerBadRequestException;

    public abstract FileHeader fetchHeaders(File file) throws TrackerException;

}
