package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.HiddenInput;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.nodes.ResizeHandle;
import com.ssplugins.preedit.util.Util;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class ImageModule extends Module {
	
	private Image image;
	
	public void setImage(Image image) {
		this.image = image;
		if (image != null) {
			getInputs().getInput("Location", LocationInput.class).ifPresent(input -> {
				input.widthProperty().set((int) image.getWidth());
				input.heightProperty().set((int) image.getHeight());
			});
		}
		getInputs().getInput("hidden", HiddenInput.class).ifPresent(HiddenInput::callUpdate);
	}
	
	@Override
	public void linkResizeHandle(ResizeHandle handle) {
		getInputs().getInput("Location", LocationInput.class).ifPresent(handle::link);
	}
	
	@Override
	public void draw(Canvas canvas, GraphicsContext context, boolean editor) throws SilentFailException {
		Bounds bounds = getInputs().getValue("Location", LocationInput.class);
		EditorCanvas.rotate(context, Util.centerX(bounds), Util.centerY(bounds), bounds.getMinZ());
		context.drawImage(image, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		map.addInput("Location", new LocationInput(true, true));
		map.addInput("hidden", new HiddenInput());
	}
	
}
