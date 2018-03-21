package com.ssplugins.preedit.nodes;

import javafx.beans.InvalidationListener;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class FillCanvas extends Canvas {
	
	private Runnable onResize;
	private ScrollPane pane;
	
	public FillCanvas(Stage stage) {
		this(stage, 0, 0);
	}
	
	public FillCanvas(Stage stage, double width, double height) {
		super(width, height);
		GridPane alignment = new GridPane();
		alignment.add(this, 0, 0);
		alignment.setAlignment(Pos.CENTER);
		pane = new ScrollPane(alignment);
		pane.setMinWidth(width);
		pane.setMinHeight(height);
		pane.setMinViewportWidth(width);
		pane.setMinViewportHeight(height);
		pane.setFitToWidth(true);
		pane.setFitToHeight(true);
		alignment.prefWidthProperty().bind(pane.prefViewportWidthProperty());
		alignment.prefHeightProperty().bind(pane.prefViewportHeightProperty());
		InvalidationListener resize = observable -> {
			if (onResize != null) onResize.run();
		};
		this.widthProperty().addListener(resize);
		this.heightProperty().addListener(resize);
	}
	
	public void setOnResize(Runnable onResize) {
		this.onResize = onResize;
	}
	
	public ScrollPane getScrollPane() {
		return pane;
	}
	
}
