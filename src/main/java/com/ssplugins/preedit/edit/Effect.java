package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.sun.istack.internal.Nullable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
	
}
