package com.xperia.xpense_tracker.services;

import java.io.File;
import java.io.IOException;

public interface ExpenseService {

    void processExpenseFromFile(File file) throws IOException;
}
