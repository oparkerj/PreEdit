package com.ssplugins.meme.modules;

import com.ssplugins.meme.edit.Layer;
import com.ssplugins.meme.input.InputMap;
import com.ssplugins.meme.input.TextInput;
import javafx.scene.canvas.GraphicsContext;

public class Text extends Layer {
	
	@Override
	public String getName() {
		return "Text";
	}
	
	@Override
	public void draw(GraphicsContext context) {
		//
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		map.addInput("Content", new TextInput(true));
	}
}
