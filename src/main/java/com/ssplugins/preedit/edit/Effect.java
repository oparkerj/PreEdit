package com.ssplugins.preedit.edit;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public abstract class Effect extends Layer {
	// Effects currently don't have unique methods.
	
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
			setText(item.getName());
		}
	}
	
}
