package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.util.GridMap;
import com.ssplugins.preedit.util.SizeHandler;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Optional;

public class ResizeHandle extends AnchorPane {
	
	private static final double HANDLE_SIZE = 8;
	private static final double HALF_HANDLE = HANDLE_SIZE / 2;
	
	private Pane topLeft, topRight, bottomLeft, bottomRight, top, right, bottom, left;
	private SizeHandler handler;
	
	private NumberField x, y, width, height;
	
	public ResizeHandle() {
		Border border = new Border(new BorderStroke(Color.MAGENTA, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
		this.setBorder(border);
		topLeft = new Pane();
		setup(topLeft, border);
		setTopLeft(topLeft);
		topRight = new Pane();
		setup(topRight, border);
		setTopRight(topRight);
		bottomLeft = new Pane();
		setup(bottomLeft, border);
		setBottomLeft(bottomLeft);
		bottomRight = new Pane();
		setup(bottomRight, border);
		setBottomRight(bottomRight);
		top = new Pane();
		setTop(top);
		right = new Pane();
		setRight(right);
		bottom = new Pane();
		setBottom(bottom);
		left = new Pane();
		setLeft(left);
		this.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight, top, right, bottom, left);
		this.prefWidthProperty().bind(this.minWidthProperty());
		this.prefHeightProperty().bind(this.minHeightProperty());
		this.setCursor(Cursor.MOVE);
		handler = new SizeHandler();
		handler.outXProperty().bindBidirectional(this.layoutXProperty());
		handler.outYProperty().bindBidirectional(this.layoutYProperty());
		handler.outWidthProperty().bindBidirectional(this.minWidthProperty());
		handler.outHeightProperty().bindBidirectional(this.minHeightProperty());
		makeDraggable();
		hide();
	}
	
	private void setup(Pane pane, Border border) {
		pane.setMinWidth(HANDLE_SIZE);
		pane.setMinHeight(HANDLE_SIZE);
		pane.setBorder(border);
	}
	
	private void setTopLeft(Pane pane) {
		AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
		AnchorPane.setLeftAnchor(pane, -HALF_HANDLE);
		pane.setCursor(Cursor.NW_RESIZE);
	}
	
	private void setTopRight(Pane pane) {
		AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
		AnchorPane.setRightAnchor(pane, -HALF_HANDLE);
		pane.setCursor(Cursor.NE_RESIZE);
	}
	
	private void setBottomLeft(Pane pane) {
		AnchorPane.setBottomAnchor(pane, -HALF_HANDLE);
		AnchorPane.setLeftAnchor(pane, -HALF_HANDLE);
		pane.setCursor(Cursor.SW_RESIZE);
	}
	
	private void setBottomRight(Pane pane) {
		AnchorPane.setBottomAnchor(pane, -HALF_HANDLE);
		AnchorPane.setRightAnchor(pane, -HALF_HANDLE);
		pane.setCursor(Cursor.SE_RESIZE);
	}
	
	private void setTop(Pane pane) {
		pane.setMinHeight(HANDLE_SIZE);
		pane.setCursor(Cursor.N_RESIZE);
		AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
		AnchorPane.setLeftAnchor(pane, HALF_HANDLE);
		AnchorPane.setRightAnchor(pane, HALF_HANDLE);
	}
	
	private void setRight(Pane pane) {
		pane.setMinWidth(HANDLE_SIZE);
		pane.setCursor(Cursor.E_RESIZE);
		AnchorPane.setRightAnchor(pane, -HALF_HANDLE);
		AnchorPane.setTopAnchor(pane, HALF_HANDLE);
		AnchorPane.setBottomAnchor(pane, HALF_HANDLE);
	}
	
	private void setBottom(Pane pane) {
		pane.setMinHeight(HANDLE_SIZE);
		pane.setCursor(Cursor.S_RESIZE);
		AnchorPane.setBottomAnchor(pane, -HALF_HANDLE);
		AnchorPane.setLeftAnchor(pane, HALF_HANDLE);
		AnchorPane.setRightAnchor(pane, HALF_HANDLE);
	}
	
	private void setLeft(Pane pane) {
		pane.setMinWidth(HANDLE_SIZE);
		pane.setCursor(Cursor.W_RESIZE);
		AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
		AnchorPane.setTopAnchor(pane, HALF_HANDLE);
		AnchorPane.setBottomAnchor(pane, HALF_HANDLE);
	}
	
	private void setSizeable(boolean sizeable) {
		this.getChildren().forEach(node -> node.setVisible(sizeable));
	}
	
	public void hide() {
		this.setVisible(false);
	}
	
	private void unbind(NumberField field, Property<Number> property) {
		if (field == null) return;
		field.numberProperty().unbindBidirectional(property);
	}
	
	private void makeDraggable() {
		EventHandler<MouseEvent> clickEvent = event -> {
			Optional<Pos> op = findPos(event.getSource());
			op.ifPresent(pos -> {
				handler.begin(event.getSceneX(), event.getSceneY(), pos);
			});
		};
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, clickEvent);
		this.getChildren().forEach(node -> node.addEventFilter(MouseEvent.MOUSE_PRESSED, clickEvent));
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			handler.update(event.getSceneX(), event.getSceneY());
		});
	}
	
	private Optional<Pos> findPos(Object src) {
		if (src == this) return Optional.of(Pos.CENTER);
		else if (src == topLeft) return Optional.of(Pos.TOP_LEFT);
		else if (src == top) return Optional.of(Pos.TOP_CENTER);
		else if (src == topRight) return Optional.of(Pos.TOP_RIGHT);
		else if (src == left) return Optional.of(Pos.CENTER_LEFT);
		else if (src == right) return Optional.of(Pos.CENTER_RIGHT);
		else if (src == bottomLeft) return Optional.of(Pos.BOTTOM_LEFT);
		else if (src == bottom) return Optional.of(Pos.BOTTOM_CENTER);
		else if (src == bottomRight) return Optional.of(Pos.BOTTOM_RIGHT);
		return Optional.empty();
	}
	
	public void linkTo(LocationInput input) {
		GridMap map = input.getNode();
		unlink();
		this.setVisible(true);
		setSizeable(input.isSizeable());
		map.get("x", NumberField.class).ifPresent(field -> {
			x = field;
			this.layoutXProperty().bindBidirectional(field.numberProperty());
		});
		map.get("y", NumberField.class).ifPresent(field -> {
			y = field;
			this.layoutYProperty().bindBidirectional(field.numberProperty());
		});
		map.get("width", NumberField.class).ifPresent(field -> {
			width = field;
			this.minWidthProperty().bindBidirectional(field.numberProperty());
		});
		map.get("height", NumberField.class).ifPresent(field -> {
			height = field;
			this.minHeightProperty().bindBidirectional(field.numberProperty());
		});
	}
	
	public void unlink() {
		unbind(x, this.layoutXProperty());
		unbind(y, this.layoutYProperty());
		unbind(width, this.minWidthProperty());
		unbind(height, this.minHeightProperty());
	}
	
}
