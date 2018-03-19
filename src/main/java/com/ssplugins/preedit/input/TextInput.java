package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.util.JsonConverter;
import javafx.scene.control.TextField;

public class TextInput extends Input<TextField, String> {
	
	private boolean emptyAllowed;
	
	public TextInput(boolean emptyAllowed) {
		this.emptyAllowed = emptyAllowed;
		this.ready();
	}
	
	public static JsonConverter<String> stringConverter() {
		return new JsonConverter<String>() {
			@Override
			public JsonElement toJson(String s) {
				return new JsonPrimitive(s);
			}
			
			@Override
			public String fromJson(JsonElement element) {
				return element.getAsString();
			}
		};
	}
	
	@Override
	protected void setNodeValue(TextField node, String value) {
		node.setText(value);
	}
	
	@Override
	protected JsonConverter<String> getJsonConverter() {
		return stringConverter();
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
