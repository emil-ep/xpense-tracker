package com.xperia.xpense_tracker.models.fileProcessors;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileProcessor {

    protected Map<Integer, String> headerIndexMap;

    public abstract List<HashMap<Integer, String>> parseFile(File file);

    public void setHeaderConfiguration(Map<Integer, String> configuration){
        this.headerIndexMap = configuration;
    }
}
