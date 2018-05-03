package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.util.Range;
import com.ssplugins.preedit.util.wrapper.FilteredObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;
import java.util.function.Function;

public class NumberField extends TextField {
	
	private NumberFormat format;
	
	private ObjectProperty<Range> range = new SimpleObjectProperty<>(Range.any());
	private ObjectProperty<Number> number = new FilteredObjectProperty<>(0, numberFilter());
	
	public NumberField() {
		this(0);
	}
	
	public NumberField(Number number) {
		this(number, Range.any());
	}
	
	public NumberField(Number number, NumberFormat format) {
		this(number, Range.any(), format);
	}
	
	public NumberField(Number number, Range range) {
		this(number, range, NumberFormat.getIntegerInstance());
	}
	
	public NumberField(Number number, Range range, NumberFormat format) {
		super(number.toString());
		this.format = format;
		this.range.set(range);
		registerEvents();
		setNumber(number);
	}
	
	private Function<Number, Optional<Number>> numberFilter() {
		return n -> Optional.of(n).filter(d -> range.get() != null).map(d -> {
			try {
				return format.parse(format.format(d));
			} catch (ParseException ignored) {}
			return 0;
		}).map(d -> range.get().clamp(d.doubleValue()));
	}
	
	private void registerEvents() {
		this.setOnAction(event -> {
			formatValue();
		});
		this.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) formatValue();
		});
		this.numberProperty().addListener((observable, oldValue, newValue) -> {
			NumberField.this.setText(format.format(range.get().clamp(newValue.doubleValue())));
		});
		this.setOnKeyPressed(event -> {
		    double d = event.isControlDown() ? 0.1 : 1;
			if (event.getCode() == KeyCode.UP) {
				setNumber(getNumber().doubleValue() + d);
			}
			else if (event.getCode() == KeyCode.DOWN) {
				setNumber(getNumber().doubleValue() - d);
			}
		});
		this.range.addListener((observable, oldValue, newValue) -> {
			setNumber(newValue.clamp(getNumber().doubleValue()));
		});
	}
	
	private void formatValue() {
		try {
			String text = this.getText();
			if (text == null || text.isEmpty()) return;
			Number val = format.parse(text);
			val = range.get().clamp(val.doubleValue());
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
	
	public Range getRange() {
		return range.get();
	}
	
	public ObjectProperty<Range> rangeProperty() {
		return range;
	}
	
	public void setRange(Range range) {
		this.range.set(range);
	}
 
}
