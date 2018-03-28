package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.ColorInput;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.nodes.ResizeHandle;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Solid extends Module {
	
	@Override
	public String getName() {
		return "Solid";
	}
	
	@Override
	public void draw(Canvas canvas, GraphicsContext context) throws SilentFailException {
		Bounds region = getInputs().getValue("Location", LocationInput.class);
		Color color = getInputs().getValue("Color", ColorInput.class);
		double cx = region.getMinX() + region.getWidth() / 2;
		double cy = region.getMinY() + region.getHeight() / 2;
		EditorCanvas.rotate(context, cx, cy, region.getMinZ());
		context.setFill(color);
		context.fillRect(region.getMinX(), region.getMinY(), region.getWidth(), region.getHeight());
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		map.addInput("Color", new ColorInput());
		map.addInput("Location", new LocationInput(true, true));
	}
	
	@Override
	public void linkResizeHandle(ResizeHandle handle) {
		getInputs().getInput("Location", LocationInput.class).ifPresent(handle::linkTo);
	}
	
}
