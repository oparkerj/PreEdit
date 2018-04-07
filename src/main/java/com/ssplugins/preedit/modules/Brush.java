package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.*;
import com.ssplugins.preedit.nodes.ResizeHandle;
import com.ssplugins.preedit.util.SafePixelWriter;
import com.ssplugins.preedit.util.Util;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Brush extends Module {
	
	private ObjectProperty<WritableImage> image;
	
	@Override
	protected void preload() {
		image = new SimpleObjectProperty<>();
	}
	
	@Override
	public void onMouseEvent(MouseEvent event, boolean editor) {
		EventType<? extends MouseEvent> type = event.getEventType();
		if (type == MouseEvent.MOUSE_CLICKED || type == MouseEvent.MOUSE_DRAGGED) {
			try {
				int size = getInputs().getValue("Size", NumberInput.class).intValue();
				circle((int) event.getX(), (int) event.getY(), size);
				getInputs().getInput("hidden", HiddenInput.class).ifPresent(HiddenInput::callUpdate);
			} catch (SilentFailException ignored) {}
		}
	}
	
//	private void circle(int cx, int cy, int radius) throws SilentFailException {
//		if (image.get() == null) return;
//		int x = radius - 1;
//		int y = 0;
//		int dx = 1;
//		int dy = 1;
//		int err = dx - (radius << 1);
//		PixelWriter writer = image.get().getPixelWriter();
//		Color c = getInputs().getValue("Color", ColorInput.class);
//		while (x >= y) {
//			writer.setColor(cx + x, cy + y, c);
//			writer.setColor(cx + y, cy + x, c);
//			writer.setColor(cx - y, cy + x, c);
//			writer.setColor(cx - x, cy + y, c);
//			writer.setColor(cx - x, cy - y, c);
//			writer.setColor(cx - y, cy - x, c);
//			writer.setColor(cx + y, cy - x, c);
//			writer.setColor(cx + x, cy - y, c);
//			if (err <= 0) {
//				y++;
//				err += dy;
//				dy += 2;
//			}
//			if (err > 0) {
//				x--;
//				dx += 2;
//				err += dx - (radius << 1);
//			}
//		}
//	}
	
	private void circle(int cx, int cy, int radius) throws SilentFailException {
		if (image.get() == null) return;
		int x = radius - 1;
		int y = 0;
		int dx = 1;
		int dy = 1;
		int err = dx - (radius << 1);
		SafePixelWriter writer = new SafePixelWriter(image.get());
		Color c = getInputs().getValue("Color", ColorInput.class);
		while (x >= y) {
			for (int i = cx - x; i <= cx + x; i++) {
				writer.setColor(i, cy + y, c);
				writer.setColor(i, cy - y, c);
			}
			for (int i = cx - y; i <= cx + y; i++) {
				writer.setColor(i, cy + x, c);
				writer.setColor(i, cy - x, c);
			}
			if (err <= 0) {
				y++;
				err += dy;
				dy += 2;
			}
			if (err > 0) {
				x--;
				dx += 2;
				err += dx - (radius << 1);
			}
		}
	}
	
	@Override
	public void linkResizeHandle(ResizeHandle handle) {
		handle.hide();
	}
	
	@Override
	public String getName() {
		return "Brush";
	}
	
	@Override
	public void draw(Canvas canvas, GraphicsContext context, boolean editor) throws SilentFailException {
		if (image.get() == null) {
			image.set(new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight()));
		}
		context.drawImage(image.get(), 0, 0);
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		NumberInput size = new NumberInput(false);
		size.setValue(25);
		map.addInput("Size", size);
		map.addInput("Color", new ColorInput());
		map.addInput("hidden", new HiddenInput());
		DataInput<WritableImage> imageData = new DataInput<>(Util.imageConverter());
		imageData.valueProperty().bindBidirectional(image);
		map.addInput("data", imageData);
	}
	
}
