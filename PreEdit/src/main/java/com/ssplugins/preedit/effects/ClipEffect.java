package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.CanvasLayer;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.NumberInput;
import com.ssplugins.preedit.util.data.Range;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class ClipEffect extends Effect {
    
    private Node node;
    private Rectangle clip;
    
    private NumberInput top, right, bottom, left;
    
    @Override
    protected void preload() {
        clip = new Rectangle(0, 0, 0, 0);
    }
    
    @Override
    public void apply(CanvasLayer canvas, Node node, boolean editor) throws SilentFailException {
        this.node = node;
        Bounds bounds = node.getBoundsInParent();
        clip.setX(bounds.getMinX() + getInt(left));
        clip.setY(bounds.getMinY() + getInt(top));
        clip.setWidth(bounds.getWidth() - getInt(left) - getInt(right));
        clip.setHeight(bounds.getHeight() - getInt(top) - getInt(bottom));
        if (this.node != null) {
            this.node.setClip(clip);
        }
    }
    
    @Override
    public void reset() {
        if (node != null) {
            node.setClip(null);
        }
    }
    
    @Override
    public String getName() {
        return "Clip";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        top = new NumberInput(false);
        top.setRange(Range.lowerBound(0));
        map.addInput("Top", top);
        right = new NumberInput(false);
        right.setRange(Range.lowerBound(0));
        map.addInput("Right", right);
        bottom = new NumberInput(false);
        bottom.setRange(Range.lowerBound(0));
        map.addInput("Bottom", bottom);
        left = new NumberInput(false);
        left.setRange(Range.lowerBound(0));
        map.addInput("Left", left);
    }
    
    private int getInt(NumberInput numberInput) {
        return numberInput.getValue().map(Number::intValue).orElse(0);
    }
    
}
