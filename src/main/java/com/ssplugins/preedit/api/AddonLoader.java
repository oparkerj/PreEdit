package com.ssplugins.preedit.api;

public interface AddonLoader {
    
    String getName();
    
    void load(PreEditAPI api);
    
    void onShutdown(PreEditAPI api);
    
}
