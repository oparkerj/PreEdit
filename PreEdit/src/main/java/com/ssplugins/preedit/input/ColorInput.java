package com.ssplugins.preedit.input;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.UndoHistory;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ColorInput extends Input<ColorPicker, Color> {
    
    public ColorInput() {
        this.ready();
    }
    
    public ObjectProperty<Color> valueProperty() {
        return getNode().valueProperty();
    }
    
    @Override
    protected ColorPicker createInputNode() {
        return new ColorPicker();
    }
    
    @Override
    protected Color getNodeValue(ColorPicker node) {
        return node.getValue();
    }
    
    @Override
    protected void setNodeValue(ColorPicker node, Color value) {
        node.setValue(value);
    }
    
    @Override
    protected boolean isValid(Color value) {
        return true;
    }
    
    @Override
    protected JsonConverter<Color> getJsonConverter() {
        return new JsonConverter<Color>() {
            @Override
            public JsonElement toJson(Color color) {
                JsonArray arr = new JsonArray();
                arr.add(color.getRed());
                arr.add(color.getGreen());
                arr.add(color.getBlue());
                arr.add(color.getOpacity());
                return arr;
            }
            
            @Override
            public Color fromJson(JsonElement element) {
                if (element.isJsonNull()) return Color.WHITE;
                JsonArray json = element.getAsJsonArray();
                return Color.color(json.get(0).getAsDouble(), json.get(1).getAsDouble(), json.get(2).getAsDouble(), json.get(3).getAsDouble());
            }
        };
    }
    
    @Override
    protected void setUpdateTrigger(ColorPicker node, Runnable update) {
        node.valueProperty().addListener(observable -> update.run());
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        undoHistory.createTrigger().auto(getNode().valueProperty());
    }
}
