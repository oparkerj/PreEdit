package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.api.PreEditTab;
import javafx.scene.control.Tab;

public interface GUI {
    
    PreEditTab getGeneratorTab();
    
    PreEditTab getEditorTab();
    
    Tab editorTab();
    
    Tab generatorTab();
    
    void addTab(Tab tab);
    
    void selectTab(Tab tab);
    
}
