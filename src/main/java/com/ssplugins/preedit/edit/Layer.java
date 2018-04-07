package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class Layer {
	
	private InputMap inputs = new InputMap();
	
	protected Layer() {
		preload();
		defineInputs(inputs);
	}
	
	public abstract String getName();
	
	public abstract void draw(Canvas canvas, GraphicsContext context, boolean editor) throws SilentFailException;
	
	protected abstract void defineInputs(InputMap map);
	
	protected void preload() {}
	
	public final InputMap getInputs() {
		return inputs;
	}
	
}
