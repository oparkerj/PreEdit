package com.ssplugins.preedit.nodes;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ResizeHandle extends AnchorPane {
	
	private Bounds relative;
	
	public ResizeHandle() {
		this.relative = new BoundingBox(0, 0, 0, 0);
		Border border = new Border(new BorderStroke(Color.MAGENTA, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
		this.setBorder(border);
		Pane topLeft = new Pane();
		topLeft.setBorder(border);
		anchorTopLeft(topLeft);
		Pane topRight = new Pane();
		topRight.setBorder(border);
		anchorTopRight(topRight);
		Pane bottomLeft = new Pane();
		bottomLeft.setBorder(border);
		anchorBottomLeft(bottomLeft);
		Pane bottomRight = new Pane();
		bottomRight.setBorder(border);
		anchorBottomRight(bottomRight);
		this.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight);
		this.prefWidthProperty().bind(this.minWidthProperty());
		this.prefHeightProperty().bind(this.minHeightProperty());
	}
	
	public void setRelative(Bounds bounds) {
		this.relative = bounds;
	}
	
	public void update(int x, int y, int width, int height) {
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.relocate(x + relative.getMinX(), y + relative.getMinY());
	}
	
	private void anchorTopLeft(Node node) {
		AnchorPane.setTopAnchor(node, 0d);
		AnchorPane.setLeftAnchor(node, 0d);
	}
	
	private void anchorTopRight(Node node) {
		AnchorPane.setTopAnchor(node, 0d);
		AnchorPane.setRightAnchor(node, 0d);
	}
	
	private void anchorBottomLeft(Node node) {
		AnchorPane.setBottomAnchor(node, 0d);
		AnchorPane.setLeftAnchor(node, 0d);
	}
	
	private void anchorBottomRight(Node node) {
		AnchorPane.setBottomAnchor(node, 0d);
		AnchorPane.setRightAnchor(node, 0d);
	}
	
}
