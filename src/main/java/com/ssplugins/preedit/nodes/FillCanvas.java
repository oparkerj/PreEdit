package com.ssplugins.preedit.nodes;

import javafx.beans.InvalidationListener;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class FillCanvas extends Canvas {
	
	private Runnable onResize;
	
	public FillCanvas(Stage stage) {
		this(stage, 0, 0);
	}
	
	public FillCanvas(Stage stage, double width, double height) {
		super(width, height);
		InvalidationListener resize = observable -> {
			if (onResize != null) onResize.run();
		};
		this.widthProperty().addListener(resize);
		this.heightProperty().addListener(resize);
		stage.widthProperty().addListener((observable, oldValue, newValue) -> {
			double delta = newValue.doubleValue() - oldValue.doubleValue();
			if (Double.isNaN(delta)) return;
			this.widthProperty().set(this.getWidth() + delta);
		});
		stage.heightProperty().addListener((observable, oldValue, newValue) -> {
			double delta = newValue.doubleValue() - oldValue.doubleValue();
			if (Double.isNaN(delta)) return;
			this.heightProperty().set(this.getHeight() + delta);
		});
	}
	
	public void setOnResize(Runnable onResize) {
		this.onResize = onResize;
	}
	
}
