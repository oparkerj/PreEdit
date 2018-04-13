package com.ssplugins.preedit.util;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class UndoHistory {
    
    private int CAPACITY = 100;
    
    private List<UndoStep<?>> list = new ArrayList<>();
    private List<ObservableValue<?>> restoring = new ArrayList<>();
    
    public UndoHistory() {}
    
    public void undo() {
        UndoStep<?> step = list.get(list.size() - 1);
        step.restore();
        list.remove(step);
    }
    
    public <T> void addTrigger(Property<T> property) {
        property.addListener((observable, oldValue, newValue) -> {
            if (restoring.contains(observable)) {
                restoring.remove(observable);
                return;
            }
            if (list.size() >= CAPACITY) return;
            UndoStep<T> step = new UndoStep<>(property, oldValue);
            step.setOnRestore(() -> {
                restoring.add(observable);
            });
            list.add(step);
        });
    }
    
    private class UndoStep<T> {
        
        private Property<T> property;
        private T oldValue;
        private Runnable onRestore;
    
        public UndoStep(Property<T> property, T oldValue) {
            this.property = property;
            this.oldValue = oldValue;
        }
        
        public void restore() {
            if (onRestore != null) onRestore.run();
            property.setValue(oldValue);
        }
    
        public void setOnRestore(Runnable onRestore) {
            this.onRestore = onRestore;
        }
        
    }
    
}
