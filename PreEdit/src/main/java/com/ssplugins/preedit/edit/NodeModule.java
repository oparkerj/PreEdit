package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.util.calc.ExpandableBounds;
import javafx.scene.Node;

public abstract class NodeModule extends Module {
    
    @Override
    public void draw(CanvasLayer canvas, boolean editor) throws SilentFailException {}
    
    public void requestExpansion(ExpandableBounds viewport) throws SilentFailException {}
    
    public abstract Node getNode();
    
}
