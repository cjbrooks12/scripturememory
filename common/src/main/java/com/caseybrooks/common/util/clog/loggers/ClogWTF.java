package com.caseybrooks.common.util.clog.loggers;

import android.util.Log;

import com.caseybrooks.common.util.clog.ClogLogger;

public class ClogWTF implements ClogLogger {

    @Override
    public void log(String tag, String message) {
        Log.wtf(tag, message);
    }
}
