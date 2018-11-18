package com.ssplugins.preedit.launcher;

public class Update {
    
    private Version version;
    private String message;
    
    public Update(Version version, String message) {
        this.version = version;
        this.message = message;
    }
    
    public Version getVersion() {
        return version;
    }
    
    public String getMessage() {
        return message;
    }
    
}
