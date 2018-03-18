package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.input.TextInput;
import javafx.scene.canvas.GraphicsContext;

public class Text extends Module {
	
	@Override
	public String getName() {
		return "Text";
	}
	
	@Override
	public void draw(GraphicsContext context) throws SilentFailException {
		String content = getInputs().getValue("Content", TextInput.class);
		context.fillText(content, 0, 0);
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		map.addInput("Location", new LocationInput());
		map.addInput("Content", new TextInput(true));
	}
	
}