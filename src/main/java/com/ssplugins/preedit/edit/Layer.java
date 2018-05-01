package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.input.Input;
import com.ssplugins.preedit.input.InputMap;

public abstract class Layer {
 
    private boolean editor;
	private InputMap inputs = new InputMap();
	
	protected Layer() {
		preload();
		defineInputs(inputs);
	}
	
	public abstract String getName();
	
	protected abstract void defineInputs(InputMap map);
	
	public void onSelectionChange(boolean selected) {}
	
	protected void preload() {}
	
	public int userInputs() {
		return (int) getInputs().getInputs().values().stream().filter(Input::isUserProvided).count();
	}
    
    public void setEditor(boolean editor) {
	    this.editor = editor;
    }
    
    public boolean isEditor() {
        return editor;
    }
    
    public final InputMap getInputs() {
		return inputs;
	}
	
}
