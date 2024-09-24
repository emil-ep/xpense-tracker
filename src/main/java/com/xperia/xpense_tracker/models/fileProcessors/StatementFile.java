package com.xperia.xpense_tracker.models.fileProcessors;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StatementFile {

    protected Map<Integer, String> headerIndexMap;

    public abstract List<HashMap<Integer, Object>> parseExpenseFromFile(Map<Integer, String> headerIndexMap, File file);

}
