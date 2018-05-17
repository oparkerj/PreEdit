package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.Range;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;

public class ColorAdjustEffect extends Effect {
    
    private ColorAdjust adjust;
    
    @Override
    protected void preload() {
        adjust = new ColorAdjust();
    }
    
    @Override
    public void apply(Canvas canvas, GraphicsContext context, Node node, boolean editor) {
        quickApply(adjust, canvas, node);
    }
    
    @Override
    public void reset() {
        adjust.setInput(null);
    }
    
    @Override
    public String getName() {
        return "ColorAdjust";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Brightness", adjust.brightnessProperty(), Range.from(-1, 1), true, 0.001);
        map.addNumberProperty("Contrast", adjust.contrastProperty(), Range.from(-1, 1), true, 0.001);
        map.addNumberProperty("Hue", adjust.hueProperty(), Range.from(-1, 1), true, 0.001);
        map.addNumberProperty("Saturation", adjust.saturationProperty(), Range.from(-1, 1), true, 0.001);
    }
    
}
