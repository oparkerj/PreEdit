package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.nodes.ResizeHandle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class Layer {
	
	private InputMap inputs = new InputMap();
	
	protected Layer() {
		defineInputs(inputs);
	}
	
	public abstract String getName();
	
	public abstract void draw(Canvas canvas, GraphicsContext context) throws SilentFailException;
	
	protected abstract void defineInputs(InputMap map);
	
	public final InputMap getInputs() {
		return inputs;
	}
	
}
