package com.ssplugins.preedit.nodes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberField extends TextField {
	
	private NumberFormat format;
	
	private ObjectProperty<Number> number = new SimpleObjectProperty<>(0);
	
	public NumberField() {
		this(0, NumberFormat.getIntegerInstance());
	}
	
	public NumberField(Number number) {
		this(number, NumberFormat.getIntegerInstance());
	}
	
	public NumberField(Number number, NumberFormat format) {
		super(number.toString());
		this.format = format;
		registerEvents();
		setNumber(number);
	}
	
	private void registerEvents() {
		this.setOnAction(event -> {
			formatValue();
		});
		this.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) formatValue();
		});
		this.numberProperty().addListener((observable, oldValue, newValue) -> {
			NumberField.this.setText(format.format(newValue));
		});
	}
	
	private void formatValue() {
		try {
			String text = this.getText();
			if (text == null || text.isEmpty()) return;
			Number val = format.parse(text);
			setNumber(val);
		} catch (ParseException e) {
			setText(format.format(number.get()));
		}
	}
	
	public Number getNumber() {
		return number.get();
	}
	
	public void setNumber(Number number) {
		this.number.set(number);
	}
	
	public ObjectProperty<Number> numberProperty() {
		return number;
	}
	
}
