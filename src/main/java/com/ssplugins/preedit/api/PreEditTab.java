package com.ssplugins.preedit.api;

import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public interface PreEditTab {
    
    ToolBar getToolbar();
    
    void addToolbarNode(Node node);
    
}
