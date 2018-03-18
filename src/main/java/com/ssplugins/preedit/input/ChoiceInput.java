package com.ssplugins.preedit.input;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.List;

public class ChoiceInput<T> extends Input<ComboBox<T>, T> {
	
	private ObservableList<T> options;
	private T def;
	
	public ChoiceInput(List<T> options, T selected) {
		this.options = FXCollections.observableArrayList(options);
		this.def = selected;
		this.ready();
	}
	
	@Override
	protected ComboBox<T> createInputNode() {
		ComboBox<T> box = new ComboBox<>();
		box.setItems(options);
		if (def != null) box.setValue(def);
		return box;
	}
	
	@Override
	protected T getNodeValue(ComboBox<T> node) {
		return node.getValue();
	}
	
	@Override
	protected boolean isValid(T value) {
		return true;
	}
	
}
