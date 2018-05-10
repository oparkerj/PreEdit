package com.ssplugins.preedit.api;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface PreEditTab {
    
    ToolBar getToolbar();
    
    void addToolbarNode(Node node);
    
    BooleanBinding templateLoadedProperty();
    
    Optional<BufferedImage> renderImage();
    
}
