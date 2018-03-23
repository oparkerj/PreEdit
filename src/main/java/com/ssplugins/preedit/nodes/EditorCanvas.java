package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import javafx.beans.InvalidationListener;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class EditorCanvas extends ScrollPane {
	
	private StackPane canvasStack;
	
	public EditorCanvas(double width, double height) {
		canvasStack = new StackPane();
		setCanvasSize(width, height);
		GridPane alignment = new GridPane();
		alignment.add(canvasStack, 0, 0);
		alignment.setAlignment(Pos.CENTER);
		this.setContent(alignment);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setMinViewportWidth(width);
		this.setMinViewportHeight(height);
		this.setFitToWidth(true);
		this.setFitToHeight(true);
		alignment.prefWidthProperty().bind(this.prefViewportWidthProperty());
		alignment.prefHeightProperty().bind(this.prefViewportHeightProperty());
	}
	
	public void setCanvasSize(double width, double height) {
		canvasStack.setMinWidth(width);
		canvasStack.setMinHeight(height);
		canvasStack.setPrefWidth(width);
		canvasStack.setPrefHeight(height);
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
			m.draw(gc);
			renderEffects(m.getEffects(), gc);
		}
	}
	
	private void renderEffects(List<Effect> list, GraphicsContext context) throws SilentFailException {
		ListIterator<Effect> it = list.listIterator(list.size());
		while (it.hasPrevious()) {
			Effect e = it.previous();
			e.draw(context);
		}
	}
	
	public void clearAll() {
		canvasStack.getChildren().clear();
	}
	
	private Canvas newLayer() {
		Canvas c = new Canvas(canvasStack.getWidth(), canvasStack.getHeight());
		canvasStack.getChildren().add(c);
		return c;
	}
	
	private List<Canvas> getLayers() {
		return canvasStack.getChildren().stream().map(Canvas.class::cast).collect(Collectors.toList());
	}
	
}
