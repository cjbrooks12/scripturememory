package com.caseybrooks.common.util.clog.formatters;

import com.caseybrooks.common.util.clog.ClogFormatter;

public class ClogClass implements ClogFormatter {
    @Override
    public String getKey() {
        return "class";
    }

    @Override
    public String format(Object data) {
        return data.getClass().toString();
    }
}
