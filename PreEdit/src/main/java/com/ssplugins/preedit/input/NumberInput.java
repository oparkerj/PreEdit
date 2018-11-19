package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.nodes.NumberField;
import com.ssplugins.preedit.util.calc.UndoHistory;
import com.ssplugins.preedit.util.data.JsonConverter;
import com.ssplugins.preedit.util.data.Range;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.text.NumberFormat;

public class NumberInput extends Input<NumberField, Number> {
    
    private boolean decimal;
    private double step = 1;
    
    public NumberInput(boolean decimal) {
        this.decimal = decimal;
        this.ready();
        this.setSlideAction((node, initial, dx) -> {
            dx *= step;
            node.setNumber(initial + dx);
        }, field -> field.getNumber().doubleValue());
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
    
    public IntegerProperty intProperty() {
        IntegerProperty p = new SimpleIntegerProperty();
        p.bindBidirectional(numberProperty());
        return p;
    }
    
    public void setRange(Range range) {
        getNode().setRange(range);
    }
    
    public double getStep() {
        return step;
    }
    
    public void setStep(double step) {
        this.step = step;
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
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        undoHistory.createTrigger().auto(getNode().numberProperty());
    }
}
