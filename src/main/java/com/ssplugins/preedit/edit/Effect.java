package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.sun.istack.internal.Nullable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public abstract class Effect extends Layer {
    
    public abstract void apply(Canvas canvas, GraphicsContext context, @Nullable Node node, boolean editor) throws SilentFailException;
	
	public static Callback<ListView<Effect>, ListCell<Effect>> getCellFactory() {
		return param -> new EffectCell();
	}
	
	private static class EffectCell extends ListCell<Effect> {
		@Override
		protected void updateItem(Effect item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText("");
				return;
			}
            if (!item.isEditor() && item.userInputs() == 0) setTextFill(Color.GRAY);
            else setTextFill(Color.BLACK);
			setText(item.getName());
		}
	}
    
    public final void quickApply(javafx.scene.effect.Effect effect, Canvas canvas, Node node) {
        addEffect(canvas, effect);
        if (node != null) node.setEffect(effect);
    }
    
    private void addEffect(Node node, javafx.scene.effect.Effect effect) {
	    if (node == null) return;
        javafx.scene.effect.Effect old = node.getEffect();
        if (old == null) {
            node.setEffect(effect);
            return;
        }
        if (effect instanceof Blend) {
            ((Blend) effect).setTopInput(old);
        }
        else if (effect instanceof Bloom) {
            ((Bloom) effect).setInput(old);
        }
        else if (effect instanceof BoxBlur) {
            ((BoxBlur) effect).setInput(old);
        }
        else if (effect instanceof ColorAdjust) {
            ((ColorAdjust) effect).setInput(old);
        }
        // ColorInput (fill, no input)
        else if (effect instanceof DisplacementMap) {
            ((DisplacementMap) effect).setInput(old);
        }
        else if (effect instanceof DropShadow) {
            ((DropShadow) effect).setInput(old);
        }
        else if (effect instanceof GaussianBlur) {
            ((GaussianBlur) effect).setInput(old);
        }
        else if (effect instanceof Glow) {
            ((Glow) effect).setInput(old);
        }
        // ImageInput (passthrough, no input)
        else if (effect instanceof InnerShadow) {
            ((InnerShadow) effect).setInput(old);
        }
        else if (effect instanceof Lighting) {
            ((Lighting) effect).setContentInput(old);
        }
        else if (effect instanceof MotionBlur) {
            ((MotionBlur) effect).setInput(old);
        }
        else if (effect instanceof PerspectiveTransform) {
            ((PerspectiveTransform) effect).setInput(old);
        }
        else if (effect instanceof Reflection) {
            ((Reflection) effect).setInput(old);
        }
        else if (effect instanceof SepiaTone) {
            ((SepiaTone) effect).setInput(old);
        }
        else if (effect instanceof Shadow) {
            ((Shadow) effect).setInput(old);
        }
        node.setEffect(effect);
    }
	
}
