package com.ssplugins.preedit.input;

import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.UndoHistory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public class DataInput<T> extends Input<Node, T> {
    
    private ObjectProperty<T> t;
    private JsonConverter<T> converter;
    
    public DataInput(JsonConverter<T> converter) {
        this.converter = converter;
        this.t = new SimpleObjectProperty<>();
        this.ready();
    }
    
    public ObjectProperty<T> valueProperty() {
        return t;
    }
    
    @Override
    protected Node createInputNode() {
        return null;
    }
    
    @Override
    protected T getNodeValue(Node node) throws InvalidInputException {
        return t.get();
    }
    
    @Override
    protected void setNodeValue(Node node, T value) {
        t.set(value);
    }
    
    @Override
    protected boolean isValid(T value) {
        return true;
    }
    
    @Override
    protected JsonConverter<T> getJsonConverter() {
        return converter;
    }
    
    @Override
    protected void setUpdateTrigger(Node node, Runnable update) {
        //
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        //
    }
}
