package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.nodes.NumberField;
import com.ssplugins.preedit.util.calc.UndoHistory;
import com.ssplugins.preedit.util.data.JsonConverter;
import com.ssplugins.preedit.util.data.Range;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

public class SliderInput extends Input<GridPane, Number> {
    
    private ObjectProperty<Range> range;
    private NumberField numberField;
    private Slider slider;
    
    public SliderInput() {
        range = new SimpleObjectProperty<>(Range.from(0, 1));
        this.ready();
    }
    
    public void setRange(Range range) {
        if (range == null) return;
        this.range.set(range);
    }
    
    @Override
    protected GridPane createInputNode() {
        GridPane pane = new GridPane();
        numberField = new NumberField();
        numberField.rangeProperty().bind(range);
        pane.add(numberField, 0, 0);
        slider = new Slider();
        slider.minProperty().bind(Bindings.createObjectBinding(() -> range.get().getLowerBound(), range));
        slider.maxProperty().bind(Bindings.createObjectBinding(() -> range.get().getUpperBound(), range));
        slider.valueProperty().bindBidirectional(numberField.numberProperty());
        pane.add(slider, 0, 1);
        return pane;
    }
    
    @Override
    protected Number getNodeValue(GridPane node) {
        return numberField.getNumber();
    }
    
    @Override
    protected void setNodeValue(GridPane node, Number value) {
        numberField.setNumber(value);
    }
    
    @Override
    protected boolean isValid(Number value) {
        return value != null;
    }
    
    @Override
    protected JsonConverter<Number> getJsonConverter() {
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
    
    @Override
    protected void setUpdateTrigger(GridPane node, Runnable update) {
        slider.valueProperty().addListener(observable -> update.run());
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        undoHistory.createTrigger().auto(slider.valueProperty());
    }
    
}
