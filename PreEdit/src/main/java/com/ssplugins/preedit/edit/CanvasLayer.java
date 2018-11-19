package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.util.calc.ExpandableBounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface CanvasLayer {
    
    boolean canvasLoaded();
    
    Canvas getCanvas();
    
    GraphicsContext getGraphics();
    
    ExpandableBounds getExpandableBounds();
    
}
