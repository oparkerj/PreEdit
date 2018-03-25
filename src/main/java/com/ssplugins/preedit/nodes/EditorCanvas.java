package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class EditorCanvas extends StackPane {
	
	private Pane posPane;
	private ResizeHandle handle;
	
	public EditorCanvas(double width, double height) {
		this.prefWidthProperty().bind(this.minWidthProperty());
		this.prefHeightProperty().bind(this.minHeightProperty());
		setCanvasSize(width, height);
		posPane = new Pane();
		this.getChildren().add(posPane);
		handle = new ResizeHandle();
		posPane.getChildren().add(handle);
//		handle.update(100, 100, 100, 100);
	}
	
	public ResizeHandle getHandle() {
		return handle;
	}
	
	public void setCanvasSize(double width, double height) {
		this.setMinWidth(width);
		this.setMinHeight(height);
	}
	
	public void clearAll() {
		this.getChildren().removeIf(node -> node instanceof Canvas);
	}
	
	public void transparentLayer() {
		Canvas canvas = newLayer();
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (int x = 0; x < canvas.getWidth(); x += 5) {
			for (int y = 0; y < canvas.getHeight(); y += 5) {
				Color c = ((x + y) % 2 == 0 ? Color.WHITE : Color.LIGHTGRAY);
				gc.setFill(c);
				gc.fillRect(x, y, 5, 5);
			}
		}
	}
	
	public void renderImage(boolean transparent, List<Module> modules) throws SilentFailException {
		clearAll();
		if (transparent) transparentLayer();
		ListIterator<Module> it = modules.listIterator(modules.size());
		while (it.hasPrevious()) {
			Module m = it.previous();
			Canvas c = newLayer();
			GraphicsContext gc = c.getGraphicsContext2D();
			m.draw(c, gc);
			renderEffects(m.getEffects(), c, gc);
		}
	}
	
	private void renderEffects(List<Effect> list, Canvas c, GraphicsContext context) throws SilentFailException {
		ListIterator<Effect> it = list.listIterator(list.size());
		while (it.hasPrevious()) {
			Effect e = it.previous();
			e.draw(c, context);
		}
	}
	
	private Canvas newLayer() {
		Canvas c = new Canvas(this.getMinWidth(), this.getMinHeight());
		this.getChildren().add(this.getChildren().size() - 1, c);
		return c;
	}
	
	private List<Canvas> getLayers() {
		return this.getChildren().stream().map(Canvas.class::cast).collect(Collectors.toList());
	}
	
}
