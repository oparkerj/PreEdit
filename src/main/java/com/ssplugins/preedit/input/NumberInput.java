package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.nodes.NumberField;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.Range;
import javafx.beans.property.ObjectProperty;

import java.text.NumberFormat;

public class NumberInput extends Input<NumberField, Number> {
	
	private boolean decimal;
	
	public NumberInput(boolean decimal) {
		this.decimal = decimal;
		this.ready();
		this.setSlideAction((node, initial, dx) -> node.setNumber(initial + dx), field -> field.getNumber().doubleValue());
	}
	
	public static JsonConverter<Number> numberConverter() {
		return new JsonConverter<Number>() {
			@Override
			public JsonElement toJson(Number number) {
				return new JsonPrimitive(number);
			}
			
			@Override
			public Number fromJson(JsonElement element) {
				return element.getAsNumber();
			}
		};
	}
	
	public ObjectProperty<Number> numberProperty() {
		return getNode().numberProperty();
	}
	
	public void setRange(Range range) {
		getNode().setRange(range);
	}
	
	@Override
	protected NumberField createInputNode() {
		if (decimal) {
			return new NumberField(0, NumberFormat.getNumberInstance());
		}
		return new NumberField(0);
	}
	
	@Override
	protected Number getNodeValue(NumberField node) throws InvalidInputException {
		return node.getNumber();
	}
	
	@Override
	protected void setNodeValue(NumberField node, Number value) {
		node.setNumber(value);
	}
	
	@Override
	protected boolean isValid(Number value) {
		return value != null;
	}
	
	@Override
	protected JsonConverter<Number> getJsonConverter() {
		return numberConverter();
	}
	
	@Override
	protected void setUpdateTrigger(NumberField node, Runnable update) {
		node.numberProperty().addListener(observable -> update.run());
	}
}
