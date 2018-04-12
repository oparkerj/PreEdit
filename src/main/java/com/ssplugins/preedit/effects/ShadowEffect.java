package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;

public class ShadowEffect extends Effect {
	
	@Override
	public String getName() {
		return "DropShadow";
	}
	
	@Override
	public void apply(Canvas canvas, GraphicsContext context, Node node, boolean editor) throws SilentFailException {
		DropShadow shadow = new DropShadow();
		shadow.setOffsetX(5);
		shadow.setOffsetY(5);
		canvas.setEffect(shadow);
		if (node != null) node.setEffect(shadow);
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		//
	}
	
}
