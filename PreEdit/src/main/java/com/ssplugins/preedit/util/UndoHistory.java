package com.ssplugins.preedit.util;

import javafx.beans.property.Property;

import java.util.ArrayList;
import java.util.List;

public class UndoHistory {
    
    private int CAPACITY = 100;
    
    private List<UndoStep<?>> list = new ArrayList<>();
    
    public UndoHistory() {}
    
    public UndoTrigger newTrigger() {
        return new UndoTrigger(this);
    }
    
    public void undo() {
        UndoStep<?> step = list.get(list.size() - 1);
        step.rollback();
        list.remove(step);
    }
    
    private void push(UndoStep<?> step) {
        list.add(step);
        while (list.size() > CAPACITY) list.remove(0);
    }
    
    private class UndoStep<T> {
        
        private UndoTrigger trigger;
        private Property<T> property;
        private T oldValue;
        private Runnable onRestore;
        
        private UndoStep(UndoTrigger trigger, Property<T> property, T oldValue) {
            this.trigger = trigger;
            this.property = property;
            this.oldValue = oldValue;
        }
        
        private void rollback() {
            trigger.undo(this);
        }
        
        private void restore() {
            if (onRestore != null) onRestore.run();
            property.setValue(oldValue);
        }
        
        private void setOnRestore(Runnable onRestore) {
            this.onRestore = onRestore;
        }
        
    }
    
    public class UndoTrigger {
        private UndoHistory history;
        private boolean hold, working;
        
        public UndoTrigger(UndoHistory history) {
            this.history = history;
        }
        
        public <T> void addProperty(Property<T> property) {
            property.addListener((observable, oldValue, newValue) -> {
                if (hold || working) return;
                UndoStep<T> step = new UndoStep<>(this, property, oldValue);
                history.push(step);
            });
        }
        
        public void setHold(boolean hold) {
            this.hold = hold;
        }
        
        private void undo(UndoStep<?> step) {
            working = true;
            step.restore();
            working = false;
        }
        
    }
    
}
