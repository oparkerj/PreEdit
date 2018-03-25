package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class DropShadow extends Effect {
	
	@Override
	public String getName() {
		return "DropShadow";
	}
	
	@Override
	public void draw(Canvas canvas, GraphicsContext context) throws SilentFailException {
		javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
		shadow.setOffsetX(5);
		shadow.setOffsetY(5);
		context.applyEffect(shadow);
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		//
	}
	
}
