package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.CanvasLayer;
import com.ssplugins.preedit.util.Range;
import javafx.scene.Node;
import javafx.scene.effect.MotionBlur;

public class MotionBlurEffect extends Effect {
    
    private MotionBlur blur;
    
    @Override
    protected void preload() {
        blur = new MotionBlur();
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
        return "MotionBlur";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Radius", blur.radiusProperty(), Range.from(0, 63), true);
        map.addNumberProperty("Angle", blur.angleProperty(), null, true);
    }
    
}
