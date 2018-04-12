package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.nodes.ResizeHandle;
import com.ssplugins.preedit.util.wrapper.ShiftList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public abstract class Module extends Layer {
	
	private ShiftList<Effect> effects = new ShiftList<>();
	
	public abstract void linkResizeHandle(ResizeHandle handle);
	
	public abstract void draw(Canvas canvas, GraphicsContext context, boolean editor) throws SilentFailException;
	
	public void onMouseEvent(MouseEvent event, boolean editor) {}
	
	public final ShiftList<Effect> getEffects() {
		return effects;
	}
	
	public final void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	public final void removeEffect(int i) {
		effects.remove(i);
	}
	
	public static Callback<ListView<Module>, ListCell<Module>> getCellFactory() {
		return param -> new ModuleCell();
	}
	
	private static class ModuleCell extends ListCell<Module> {
		@Override
		protected void updateItem(Module item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText("");
				return;
			}
			setText(item.getName());
		}
	}
	
}
