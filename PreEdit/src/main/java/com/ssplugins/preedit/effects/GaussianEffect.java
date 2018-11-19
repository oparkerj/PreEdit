package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.CanvasLayer;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.data.Range;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;

public class GaussianEffect extends Effect {
    
    private GaussianBlur blur;
    
    @Override
    protected void preload() {
        blur = new GaussianBlur();
    }
    
    @Override
    public void apply(CanvasLayer canvas, Node node, boolean editor) {
        quickApply(blur, canvas, node);
    }
    
    @Override
    public void reset() {
        blur.setInput(null);
    }
    
    @Override
    public String getName() {
        return "GaussianBlur";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Radius", blur.radiusProperty(), Range.from(0, 63), true, 0.01);
    }
}
