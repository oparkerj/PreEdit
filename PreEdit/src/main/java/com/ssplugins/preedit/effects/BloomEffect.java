package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.Range;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;

public class BloomEffect extends Effect {
    
    private Bloom bloom;
    
    @Override
    protected void preload() {
        bloom = new Bloom();
    }
    
    @Override
    public void reset() {
        bloom.setInput(null);
    }
    
    @Override
    public void apply(Canvas canvas, GraphicsContext context, Node node, boolean editor) {
        quickApply(bloom, canvas, node);
    }
    
    @Override
    public String getName() {
        return "Bloom";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Threshold", bloom.thresholdProperty(), Range.from(0, 1), true, 0.005);
    }
    
}
