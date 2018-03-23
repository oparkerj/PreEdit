package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.ColorInput;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.input.LocationInput.Region;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Solid extends Module {
	
	@Override
	public String getName() {
		return "Solid";
	}
	
	@Override
	public void draw(GraphicsContext context) throws SilentFailException {
		Region region = getInputs().getValue("Location", LocationInput.class);
		Color color = getInputs().getValue("Color", ColorInput.class);
		context.setFill(color);
		context.fillRect(region.getX(), region.getY(), region.getWidth(), region.getHeight());
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		map.addInput("Color", new ColorInput());
		map.addInput("Location", new LocationInput(true));
	}
	
}
