package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.nodes.NumberField;
import com.ssplugins.preedit.util.GridMap;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.Util;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;

import java.text.NumberFormat;

public class LocationInput extends Input<GridMap, Bounds> {
	
	private int x, y, width, height, angle;
	private boolean size, rotate;
	
	public LocationInput(boolean size, boolean rotate) {
		this(size, rotate, 0, 0, 100, 100);
	}
	
	public LocationInput(boolean size, boolean rotate, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.size = size;
		this.rotate = rotate;
		this.ready();
	}
	
	public boolean isSizeable() {
		return size;
	}
	
	public boolean canSpin() {
		return rotate;
	}
	
	@Override
	protected void setNodeValue(GridMap node, Bounds value) {
		node.get("x", NumberField.class).ifPresent(field -> field.setNumber(value.getMinX()));
		node.get("y", NumberField.class).ifPresent(field -> field.setNumber(value.getMinY()));
		node.get("width", NumberField.class).ifPresent(field -> field.setNumber(value.getWidth()));
		node.get("height", NumberField.class).ifPresent(field -> field.setNumber(value.getHeight()));
		node.get("angle", NumberField.class).ifPresent(field -> field.setNumber(value.getMinZ()));
	}
	
	@Override
	protected void setUpdateTrigger(GridMap node, Runnable update) {
		node.get("x", NumberField.class).ifPresent(textField -> textField.numberProperty().addListener(observable -> update.run()));
		node.get("y", NumberField.class).ifPresent(textField -> textField.numberProperty().addListener(observable -> update.run()));
		node.get("width", NumberField.class).ifPresent(textField -> textField.numberProperty().addListener(observable -> update.run()));
		node.get("height", NumberField.class).ifPresent(textField -> textField.numberProperty().addListener(observable -> update.run()));
		node.get("angle", NumberField.class).ifPresent(textField -> textField.numberProperty().addListener(observable -> update.run()));
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
				out.addProperty("angle", bounds.getMinZ());
				return out;
			}
			
			@Override
			public Bounds fromJson(JsonElement element) {
				if (element.isJsonNull()) return new BoundingBox(0, 0, 100, 100);
				JsonObject json = element.getAsJsonObject();
				return new BoundingBox(json.get("x").getAsInt(),
									   json.get("y").getAsInt(),
									   json.get("angle").getAsDouble(),
									   json.get("width").getAsInt(),
									   json.get("height").getAsInt(),
									   0);
			}
		};
	}
	
	@Override
	protected GridMap createInputNode() {
		GridMap map = new GridMap();
		map.setHgap(5);
		map.setVgap(5);
		map.add(null, 0, 0, new Label("x:"));
		NumberField fieldX = new NumberField(x);
		fieldX.setPrefWidth(50);
		map.add("x", 0, 1, fieldX);
		map.add(null, 0, 2, new Label("y:"));
		NumberField fieldY = new NumberField(y);
		fieldY.setPrefWidth(50);
		map.add("y", 0, 3, fieldY);
		if (size) {
			map.add(null, 1, 0, new Label("width:"));
			NumberField fieldW = new NumberField(width);
			fieldW.setPrefWidth(50);
			map.add("width", 1, 1, fieldW);
			map.add(null, 1, 2, new Label("height:"));
			NumberField fieldH = new NumberField(height);
			fieldH.setPrefWidth(50);
			map.add("height", 1, 3, fieldH);
		}
		if (rotate) {
			map.add(null, 2, 0, new Label("angle:"));
			NumberField fieldR = new NumberField(angle, NumberFormat.getNumberInstance());
			fieldR.setPrefWidth(50);
			map.add("angle", 2, 1, fieldR);
		}
		return map;
	}
	
	@Override
	protected Bounds getNodeValue(GridMap node) throws InvalidInputException {
		try {
			int x = node.get("x", NumberField.class).map(NumberField::getNumber).map(Number::intValue).orElseThrow(Util.invalidInput());
			int y = node.get("y", NumberField.class).map(NumberField::getNumber).map(Number::intValue).orElseThrow(Util.invalidInput());
			int width = node.get("width", NumberField.class).map(NumberField::getNumber).map(Number::intValue).orElseThrow(Util.invalidInput());
			int height = node.get("height", NumberField.class).map(NumberField::getNumber).map(Number::intValue).orElseThrow(Util.invalidInput());
			double angle = node.get("angle", NumberField.class).map(NumberField::getNumber).map(Number::doubleValue).orElseThrow(Util.invalidInput());
			return new BoundingBox(x, y, angle, width, height, 0);
		} catch (NumberFormatException e) {
			throw new InvalidInputException();
		}
	}
	
	@Override
	protected boolean isValid(Bounds value) {
		return value.getWidth() > 0 && value.getHeight() > 0;
	}
	
}
