package com.ssplugins.preedit.input;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.util.Range;
import com.ssplugins.preedit.util.Util;
import javafx.beans.property.Property;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InputMap {
	
	private Map<String, Input> inputs = new HashMap<>();
	
	public int size() {
		return inputs.size();
	}
	
	public <I extends Input> void addInput(String name, I input) {
		if (!input.isReady()) throw new IllegalArgumentException("Invalid input element. (Not ready)");
		inputs.put(name, input);
	}
	
	public Map<String, Input> getInputs() {
		return Collections.unmodifiableMap(inputs);
	}
	
	public Optional<Input> getInput(String name) {
		return Optional.of(inputs.get(name));
	}
	
	public <T extends Input> Optional<T> getInput(String name, Class<T> type) {
		return getInput(name).filter(input -> type.isAssignableFrom(input.getClass())).map(type::cast);
	}
	
	public <T> T getValue(String name, Class<? extends Input<?, T>> type) throws SilentFailException {
		return getInput(name, type).flatMap(Input::getValue).orElseThrow(Util.silentFail());
	}
    
    public void addNumberProperty(String name, Property<Number> property, Range range, boolean decimal) {
        addNumberProperty(name, property, range, decimal, 1);
    }
    
    public void addNumberProperty(String name, Property<Number> property, Range range, boolean decimal, double step) {
        NumberInput input = new NumberInput(decimal);
        input.setValue(property.getValue());
        input.setStep(step);
        if (range != null) input.setRange(range);
        property.bind(input.numberProperty());
        addInput(name, input);
    }
	
}
