package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.JsonConverter;
import javafx.scene.control.CheckBox;

public class CheckboxInput extends Input<CheckBox, Boolean> {
    
    private String text;
    
    public CheckboxInput() {
        this("");
    }
    
    public CheckboxInput(String text) {
        this.text = text;
        this.ready();
    }
    
    @Override
    protected CheckBox createInputNode() {
        return new CheckBox(text);
    }
    
    @Override
    protected Boolean getNodeValue(CheckBox node) throws InvalidInputException {
        return node.isSelected();
    }
    
    @Override
    protected void setNodeValue(CheckBox node, Boolean value) {
        node.setSelected(value);
    }
    
    @Override
    protected boolean isValid(Boolean value) {
        return true;
    }
    
    @Override
    protected JsonConverter<Boolean> getJsonConverter() {
        return new JsonConverter<Boolean>() {
            @Override
            public JsonElement toJson(Boolean b) {
                return new JsonPrimitive(b);
            }
            
            @Override
            public Boolean fromJson(JsonElement element) {
                return element.getAsBoolean();
            }
        };
    }
    
    @Override
    protected void setUpdateTrigger(CheckBox node, Runnable update) {
        node.selectedProperty().addListener(observable -> update.run());
    }
}
