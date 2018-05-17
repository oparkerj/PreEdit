package com.ssplugins.preedit.util.wrapper;

import javafx.beans.property.SimpleObjectProperty;

import java.util.Optional;

public class OptionalProperty<T> extends SimpleObjectProperty<T> {
    
    public OptionalProperty() {}
    
    public OptionalProperty(T initialValue) {
        super(initialValue);
    }
    
    public OptionalProperty(Object bean, String name) {
        super(bean, name);
    }
    
    public OptionalProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }
    
    public Optional<T> optional() {
        return Optional.ofNullable(get());
    }
    
}
