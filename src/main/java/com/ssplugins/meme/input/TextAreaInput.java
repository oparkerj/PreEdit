package com.ssplugins.meme.input;

import javafx.scene.control.TextArea;

public class TextAreaInput extends Input<TextArea, String> {
	
	private boolean emptyAllowed;
	
	public TextAreaInput(boolean emptyAllowed) {
		this.emptyAllowed = emptyAllowed;
		this.ready();
	}
	
	@Override
	protected TextArea createInputNode() {
		return new TextArea();
	}
	
	@Override
	protected String getNodeValue(TextArea node) {
		return node.getText();
	}
	
	@Override
	protected boolean isValid(String value) {
		return emptyAllowed || value.isEmpty();
	}
	
}
