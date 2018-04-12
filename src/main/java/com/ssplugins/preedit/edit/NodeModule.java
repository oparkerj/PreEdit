package com.ssplugins.preedit.edit;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class NodeModule extends Module {
    
    @Override
    public void draw(Canvas canvas, GraphicsContext context, boolean editor) {}
    
    public abstract Node getNode();
    
}
