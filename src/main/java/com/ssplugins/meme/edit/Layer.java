package com.ssplugins.meme.edit;

import com.ssplugins.meme.input.InputMap;
import javafx.scene.canvas.GraphicsContext;

public abstract class Layer {
	
	private InputMap inputs = new InputMap();
	
	protected Layer() {
		defineInputs(inputs);
	}
	
	public abstract String getName();
	
	public abstract void draw(GraphicsContext context);
	
	protected abstract void defineInputs(InputMap map);
	
	public final InputMap getInputs() {
		return inputs;
	}
	
}
