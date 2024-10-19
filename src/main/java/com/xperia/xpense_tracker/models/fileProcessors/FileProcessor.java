package com.xperia.xpense_tracker.models.fileProcessors;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.exception.customexception.TrackerException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public abstract class FileProcessor {


    public abstract List<HashMap<Integer, String>> parseFile(File file) throws TrackerBadRequestException;

    public abstract List<String> fetchHeaders(File file) throws TrackerException;

}
