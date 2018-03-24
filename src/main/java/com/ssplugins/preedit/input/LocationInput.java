package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.GridMap;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.Util;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public class LocationInput extends Input<GridMap, Bounds> {
	
	private int x, y, width, height;
	private boolean size;
	
	public LocationInput(boolean size) {
		this(size, 0, 0, 100, 100);
	}
	
	public LocationInput(boolean size, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.size = size;
		this.ready();
	}
	
	@Override
	protected void setNodeValue(GridMap node, Bounds value) {
		node.get("x", TextField.class).ifPresent(field -> field.setText(String.valueOf(value.getMinX())));
		node.get("y", TextField.class).ifPresent(field -> field.setText(String.valueOf(value.getMinY())));
		node.get("width", TextField.class).ifPresent(field -> field.setText(String.valueOf(value.getWidth())));
		node.get("height", TextField.class).ifPresent(field -> field.setText(String.valueOf(value.getHeight())));
	}
	
	@Override
	protected void setUpdateTrigger(GridMap node, Runnable update) {
		node.get("x", TextField.class).ifPresent(textField -> textField.textProperty().addListener(observable -> update.run()));
		node.get("y", TextField.class).ifPresent(textField -> textField.textProperty().addListener(observable -> update.run()));
		node.get("width", TextField.class).ifPresent(textField -> textField.textProperty().addListener(observable -> update.run()));
		node.get("height", TextField.class).ifPresent(textField -> textField.textProperty().addListener(observable -> update.run()));
	}
	
	@Override
	protected JsonConverter<Bounds> getJsonConverter() {
		return new JsonConverter<Bounds>() {
			@Override
			public JsonElement toJson(Bounds bounds) {
				JsonObject out = new JsonObject();
				out.addProperty("x", bounds.getMinX());
				out.addProperty("y", bounds.getMinY());
				out.addProperty("width", bounds.getWidth());
				out.addProperty("height", bounds.getHeight());
				return out;
			}
			
			@Override
			public Bounds fromJson(JsonElement element) {
				if (element.isJsonNull()) return new BoundingBox(0, 0, 100, 100);
				JsonObject json = element.getAsJsonObject();
				return new BoundingBox(json.get("x").getAsInt(),
								  json.get("y").getAsInt(),
								  json.get("width").getAsInt(),
								  json.get("height").getAsInt());
			}
		};
	}
	
	@Override
	protected GridMap createInputNode() {
		GridMap map = new GridMap();
		map.setHgap(5);
		map.setVgap(5);
		map.add(null, 0, 0, new Label("x:"));
		TextField fieldX = new TextField(String.valueOf(x));
		fieldX.setPrefWidth(50);
		map.add("x", 0, 1, fieldX);
		map.add(null, 0, 2, new Label("y:"));
		TextField fieldY = new TextField(String.valueOf(y));
		fieldY.setPrefWidth(50);
		map.add("y", 0, 3, fieldY);
		if (!size) return map;
		map.add(null, 1, 0, new Label("width:"));
		TextField fieldW = new TextField(String.valueOf(width));
		fieldW.setPrefWidth(50);
		map.add("width", 1, 1, fieldW);
		map.add(null, 1, 2, new Label("height:"));
		TextField fieldH = new TextField(String.valueOf(height));
		fieldH.setPrefWidth(50);
		map.add("height", 1, 3, fieldH);
		return map;
	}
	
	@Override
	protected Bounds getNodeValue(GridMap node) throws InvalidInputException {
		try {
			int x = node.get("x", TextField.class).map(TextInputControl::getText).map(Integer::parseInt).orElseThrow(Util.invalidInput());
			int y = node.get("y", TextField.class).map(TextInputControl::getText).map(Integer::parseInt).orElseThrow(Util.invalidInput());
			int width = node.get("width", TextField.class).map(TextInputControl::getText).map(Integer::parseInt).orElseThrow(Util.invalidInput());
			int height = node.get("height", TextField.class).map(TextInputControl::getText).map(Integer::parseInt).orElseThrow(Util.invalidInput());
			return new BoundingBox(x, y, width, height);
		} catch (NumberFormatException e) {
			throw new InvalidInputException();
		}
	}
	
	@Override
	protected boolean isValid(Bounds value) {
		return true;
	}
	
}
