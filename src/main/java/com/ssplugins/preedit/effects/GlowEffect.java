package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.Range;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;

public class GlowEffect extends Effect {
    
    private Glow glow;
    
    @Override
    protected void preload() {
        glow = new Glow();
    }
    
    @Override
    public void apply(Canvas canvas, GraphicsContext context, Node node, boolean editor) {
        quickApply(glow, canvas, node);
    }
    
    @Override
    public void reset() {
        glow.setInput(null);
    }
    
    @Override
    public String getName() {
        return "Glow";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Level", glow.levelProperty(), Range.from(0, 1), true, 0.001);
    }
    
}
