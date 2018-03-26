package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.util.GridMap;
import com.ssplugins.preedit.util.SizeHandler;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
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
		setupAnchor(topLeft, Pos.TOP_LEFT, Cursor.NW_RESIZE, border);
		topRight = new Pane();
		setupAnchor(topRight, Pos.TOP_RIGHT, Cursor.NE_RESIZE, border);
		bottomLeft = new Pane();
		setupAnchor(bottomLeft, Pos.BOTTOM_LEFT, Cursor.SW_RESIZE, border);
		bottomRight = new Pane();
		setupAnchor(bottomRight, Pos.BOTTOM_RIGHT, Cursor.SE_RESIZE, border);
		top = new Pane();
		setupAnchor(top, Pos.TOP_CENTER, Cursor.N_RESIZE, null);
		right = new Pane();
		setupAnchor(right, Pos.CENTER_RIGHT, Cursor.E_RESIZE, null);
		bottom = new Pane();
		setupAnchor(bottom, Pos.BOTTOM_CENTER, Cursor.S_RESIZE, null);
		left = new Pane();
		setupAnchor(left, Pos.CENTER_LEFT, Cursor.W_RESIZE, null);
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
	
	private void setupAnchor(Pane pane, Pos anchor, Cursor cursor, Border border) {
		pane.setMinWidth(HANDLE_SIZE);
		pane.setMinHeight(HANDLE_SIZE);
		if (border != null) pane.setBorder(border);
		if (cursor != null) pane.setCursor(cursor);
		switch (anchor) {
			case TOP_CENTER:
				AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
				AnchorPane.setLeftAnchor(pane, HALF_HANDLE);
				AnchorPane.setRightAnchor(pane, HALF_HANDLE);
				break;
			case TOP_LEFT:
				AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
				AnchorPane.setLeftAnchor(pane, -HALF_HANDLE);
				break;
			case TOP_RIGHT:
				AnchorPane.setTopAnchor(pane, -HALF_HANDLE);
				AnchorPane.setRightAnchor(pane, -HALF_HANDLE);
				break;
			case CENTER_LEFT:
				AnchorPane.setLeftAnchor(pane, -HALF_HANDLE);
				AnchorPane.setTopAnchor(pane, HALF_HANDLE);
				AnchorPane.setBottomAnchor(pane, HALF_HANDLE);
				break;
			case CENTER_RIGHT:
				AnchorPane.setRightAnchor(pane, -HALF_HANDLE);
				AnchorPane.setTopAnchor(pane, HALF_HANDLE);
				AnchorPane.setBottomAnchor(pane, HALF_HANDLE);
				break;
			case BOTTOM_LEFT:
				AnchorPane.setBottomAnchor(pane, -HALF_HANDLE);
				AnchorPane.setLeftAnchor(pane, -HALF_HANDLE);
				break;
			case BOTTOM_CENTER:
				AnchorPane.setBottomAnchor(pane, -HALF_HANDLE);
				AnchorPane.setLeftAnchor(pane, HALF_HANDLE);
				AnchorPane.setRightAnchor(pane, HALF_HANDLE);
				break;
			case BOTTOM_RIGHT:
				AnchorPane.setBottomAnchor(pane, -HALF_HANDLE);
				AnchorPane.setRightAnchor(pane, -HALF_HANDLE);
				break;
			default:
				break;
		}
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
