package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.input.InputMap;

public abstract class Layer {
	
	private InputMap inputs = new InputMap();
	
	protected Layer() {
		preload();
		defineInputs(inputs);
	}
	
	public abstract String getName();
	
	protected abstract void defineInputs(InputMap map);
	
	public void onSelectionChange(boolean selected) {}
	
	protected void preload() {}
	
	public final InputMap getInputs() {
		return inputs;
	}
	
}
