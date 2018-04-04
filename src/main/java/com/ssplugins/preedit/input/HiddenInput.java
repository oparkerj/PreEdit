package com.ssplugins.preedit.input;

import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.JsonConverter;
import javafx.scene.Node;

public class HiddenInput extends Input<Node, Void> {
	
	private Runnable update;
	
	public HiddenInput() {
		this.ready();
	}
	
	public void callUpdate() {
		if (update != null) update.run();
	}
	
	@Override
	protected Node createInputNode() {
		return null;
	}
	
	@Override
	protected Void getNodeValue(Node node) throws InvalidInputException {
		return null;
	}
	
	@Override
	protected void setNodeValue(Node node, Void value) {
		//
	}
	
	@Override
	protected boolean isValid(Void value) {
		return true;
	}
	
	@Override
	protected JsonConverter<Void> getJsonConverter() {
		return JsonConverter.empty();
	}
	
	@Override
	protected void setUpdateTrigger(Node node, Runnable update) {
		this.update = update;
	}
	
}
