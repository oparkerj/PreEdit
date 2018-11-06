package com.ssplugins.preedit.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface CanvasLayer {
    
    boolean canvasLoaded();
    
    Canvas getCanvas();
    
    GraphicsContext getGraphics();
    
    ExpandableBounds getExpandableBounds();
    
}
