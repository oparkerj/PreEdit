package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.nodes.ResizeHandle;
import com.ssplugins.preedit.util.ShiftList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public abstract class Module extends Layer {
	
	private ShiftList<Effect> effects = new ShiftList<>();
	
	public abstract void linkResizeHandle(ResizeHandle handle);
	
	public final ShiftList<Effect> getEffects() {
		return effects;
	}
	
	public final void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	public final void removeEffect(int i) {
		effects.remove(i);
	}
	
	public final void shiftEffect(int i, boolean up) {
		effects.shiftElement(i, !up);
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
