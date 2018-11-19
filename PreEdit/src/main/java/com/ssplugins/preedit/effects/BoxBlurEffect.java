package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.CanvasLayer;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.data.Range;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;

public class BoxBlurEffect extends Effect {
    
    private BoxBlur blur;
    
    @Override
    protected void preload() {
        blur = new BoxBlur();
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
        return "BoxBlur";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Width", blur.widthProperty(), Range.from(0, 255), true);
        map.addNumberProperty("Height", blur.heightProperty(), Range.from(0, 255), true);
        map.addNumberProperty("Iterations", blur.iterationsProperty(), Range.from(0, 3), false);
    }
}
