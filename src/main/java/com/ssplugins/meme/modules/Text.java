package com.ssplugins.meme.modules;

import com.ssplugins.meme.edit.Layer;
import com.ssplugins.meme.exceptions.SilentFailException;
import com.ssplugins.meme.input.InputMap;
import com.ssplugins.meme.input.LocationInput;
import com.ssplugins.meme.input.TextInput;
import javafx.scene.canvas.GraphicsContext;

public class Text extends Layer {
	
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
