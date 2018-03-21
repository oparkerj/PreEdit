package com.ssplugins.preedit.input;

import com.ssplugins.preedit.util.JsonConverter;
import javafx.scene.control.TextArea;

public class TextAreaInput extends Input<TextArea, String> {
	
	private boolean emptyAllowed;
	
	public TextAreaInput(boolean emptyAllowed) {
		this.emptyAllowed = emptyAllowed;
		this.ready();
	}
	
	@Override
	protected void setNodeValue(TextArea node, String value) {
		node.setText(value);
	}
	
	@Override
	protected JsonConverter<String> getJsonConverter() {
		return TextInput.stringConverter();
	}
	
	@Override
	protected void setUpdateTrigger(TextArea node, Runnable update) {
		node.textProperty().addListener(observable -> update.run());
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
