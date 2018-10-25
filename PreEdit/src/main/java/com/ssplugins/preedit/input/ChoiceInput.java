package com.ssplugins.preedit.input;

import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.UndoHistory;
import com.ssplugins.preedit.util.Util;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.List;

public class ChoiceInput<T> extends Input<ComboBox<T>, T> {
    
    private ObservableList<T> options;
    private T def;
    private Callback<ListView<T>, ListCell<T>> cellFactory;
    private JsonConverter<T> converter;
    
    public ChoiceInput(ObservableList<T> options, T selected, JsonConverter<T> converter) {
        this.options = options;
        this.def = selected;
        this.converter = converter;
        this.ready();
    }
    
    public ChoiceInput(List<T> options, T selected, JsonConverter<T> converter) {
        this(FXCollections.observableArrayList(options), selected, converter);
    }
    
    public ChoiceInput(T[] ts, T selected, JsonConverter<T> converter) {
        this(FXCollections.observableArrayList(ts), selected, converter);
    }
    
    public static <U> U getChoice(InputMap map, String name, Class<U> type) throws SilentFailException {
        return map.getInput(name, ChoiceInput.class).flatMap(Input::getValue).filter(o -> type.isAssignableFrom(o.getClass())).map(type::cast).orElseThrow(Util.silentFail());
    }
    
    public void setCellFactory(Callback<ListView<T>, ListCell<T>> cellFactory) {
        this.cellFactory = cellFactory;
    }
    
    public ObjectProperty<T> valueProperty() {
        return getNode().valueProperty();
    }
    
    @Override
    protected void setNodeValue(ComboBox<T> node, T value) {
        node.setValue(value);
    }
    
    @Override
    protected JsonConverter<T> getJsonConverter() {
        return converter;
    }
    
    @Override
    protected void setUpdateTrigger(ComboBox<T> node, Runnable update) {
        node.valueProperty().addListener(observable -> update.run());
    }
    
    @Override
    protected ComboBox<T> createInputNode() {
        ComboBox<T> box = new ComboBox<>();
        if (cellFactory != null) box.setCellFactory(cellFactory);
        box.setItems(options);
        if (def != null) box.setValue(def);
        return box;
    }
    
    @Override
    protected T getNodeValue(ComboBox<T> node) {
        return node.getValue();
    }
    
    @Override
    protected boolean isValid(T value) {
        return value != null;
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        undoHistory.createTrigger().auto(getNode().valueProperty());
    }
}
