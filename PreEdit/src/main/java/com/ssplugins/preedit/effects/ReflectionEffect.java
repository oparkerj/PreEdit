package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.Range;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Reflection;

public class ReflectionEffect extends Effect {
    
    private Reflection reflection;
    
    @Override
    protected void preload() {
        reflection = new Reflection();
    }
    
    @Override
    public void apply(Canvas canvas, GraphicsContext context, Node node, boolean editor) {
        quickApply(reflection, canvas, node);
    }
    
    @Override
    public void reset() {
        reflection.setInput(null);
    }
    
    @Override
    public String getName() {
        return "Reflection";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        map.addNumberProperty("Top Opacity", reflection.topOpacityProperty(), Range.from(0, 1), true, 0.001);
        map.addNumberProperty("Bottom Opacity", reflection.bottomOpacityProperty(), Range.from(0, 1), true, 0.001);
        map.addNumberProperty("Top Offset", reflection.topOffsetProperty(), null, true);
        map.addNumberProperty("Fraction", reflection.fractionProperty(), Range.from(0, 1), true, 0.001);
    }
}
