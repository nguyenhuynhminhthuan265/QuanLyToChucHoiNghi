package com.javafx.security;

import java.util.logging.LogRecord;

public class Filter implements java.util.logging.Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}
