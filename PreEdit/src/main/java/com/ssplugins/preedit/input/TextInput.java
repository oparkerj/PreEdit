package com.ssplugins.preedit.input;

import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.UndoHistory;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

public class TextInput extends Input<TextField, String> {
    
    private boolean emptyAllowed;
    
    public TextInput(boolean emptyAllowed) {
        this.emptyAllowed = emptyAllowed;
        this.ready();
    }
    
    public StringProperty textProperty() {
        return getNode().textProperty();
    }
    
    @Override
    protected void setNodeValue(TextField node, String value) {
        node.setText(value);
    }
    
    @Override
    protected void setUpdateTrigger(TextField node, Runnable update) {
        node.textProperty().addListener(observable -> update.run());
    }
    
    @Override
    protected JsonConverter<String> getJsonConverter() {
        return JsonConverter.forString();
    }
    
    @Override
    protected TextField createInputNode() {
        return new TextField();
    }
    
    @Override
    protected String getNodeValue(TextField node) {
        return node.getText();
    }
    
    @Override
    protected boolean isValid(String value) {
        if (emptyAllowed) return true;
        return !value.isEmpty();
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        undoHistory.createTrigger().auto(getNode().textProperty());
    }
}
