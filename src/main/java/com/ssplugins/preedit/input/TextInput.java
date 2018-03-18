package com.ssplugins.preedit.input;

import javafx.scene.control.TextField;

public class TextInput extends Input<TextField, String> {
	
	private boolean emptyAllowed;
	
	public TextInput(boolean emptyAllowed) {
		this.emptyAllowed = emptyAllowed;
		this.ready();
	}
	
	@Override
	protected TextField createInputNode() {
		return new TextField();
	}
	
	@Override
	protected String getNodeValue(TextField node) {
		return node.getText();
	}
	
	@Override
	protected boolean isValid(String value) {
		return emptyAllowed || !value.isEmpty();
	}
	
}
