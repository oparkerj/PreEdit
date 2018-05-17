package com.ssplugins.preedit.input;

import com.ssplugins.preedit.util.Util;

public class URLInput extends TextInput {
    
    public URLInput() {
        super(false);
    }
    
    @Override
    protected boolean isValid(String value) {
        return super.isValid(value) && Util.validURL(value);
    }
    
}
