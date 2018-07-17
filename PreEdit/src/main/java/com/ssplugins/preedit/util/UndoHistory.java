package com.ssplugins.preedit.util;

import javafx.beans.property.Property;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UndoHistory {
    
    private int capacity = 200;
    private int pointer = -1;
    private boolean working;
    
    private List<UndoStep<?>> list = new ArrayList<>();
    
    private boolean mouseDown;
    private List<KeyCode> keys = new ArrayList<>();
    
    public UndoHistory(Stage stage) {
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            mouseDown = true;
        });
        stage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            mouseDown = false;
        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            keys.add(event.getCode());
        });
        stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            keys.remove(event.getCode());
        });
    }
    
    public boolean isButtonHeld() {
        if (mouseDown) return true;
        if (keys.contains(KeyCode.UP)) return true;
        if (keys.contains(KeyCode.DOWN)) return true;
        if (keys.contains(KeyCode.LEFT)) return true;
        return keys.contains(KeyCode.RIGHT);
    }
    
    public boolean isWorking() {
        return working; // todo this
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        while (list.size() > capacity) {
            list.remove(0);
        }
    }
    
    public <T> UndoTrigger<T> createTrigger() {
        return new UndoTrigger<>(this);
    }
    
    public void undo() {
        synchronized (this) {
            if (list.size() == 0) return;
            if (working) return;
            working = true;
            boolean push = false;
            if (pointer == -1) {
                push = true;
                pointer = list.size();
            }
            else pointer--;
            if (pointer < 0) {
                pointer = 0;
                return;
            }
            UndoStep<?> step = list.get(pointer);
            if (push) list.add(step.currentStep());
            step.restore();
            working = false;
        }
    }
    
    public void redo() {
        synchronized (this) {
            if (working) return;
            working = true;
            if (pointer == -1) return;
            pointer++;
            if (pointer == list.size()) {
                pointer--;
                return;
            }
            UndoStep<?> step = list.get(pointer);
            step.restore();
            working = false;
        }
    }
    
    private void push(UndoStep<?> step) {
        if (pointer > -1) {
            pointer++;
            while (list.size() > pointer) list.remove(pointer);
            pointer = -1;
        }
        list.add(step);
        while (list.size() > capacity) list.remove(0);
    }
    
    private class UndoStep<T> {
        
        private Property<T> property;
        private T oldValue;
        
        private UndoStep(Property<T> property, T oldValue) {
            this.property = property;
            this.oldValue = oldValue;
        }
        
        private UndoStep<T> currentStep() {
            return new UndoStep<>(property, property.getValue());
        }
        
        private void restore() {
            property.setValue(oldValue);
        }
        
    }
    
    public class UndoTrigger<T> {
    
        private UndoHistory history;
        private T data;
        
        // Custom settings
        private long last;
        private long interval;
        private boolean bypass;
        private boolean heldUsed;
    
        public UndoTrigger(UndoHistory history) {
            this.history = history;
        }
    
        public T getData() {
            return data;
        }
    
        public void setData(T data) {
            this.data = data;
        }
    
        public long getInterval() {
            return interval;
        }
    
        public void setInterval(long interval) {
            this.interval = interval;
        }
        
        public void bypassHold() {
            bypass = true;
        }
    
        public <U> void submit(Property<U> property, U oldValue) {
            if (history.isWorking()) return;
            if (history.isButtonHeld() && !bypass) {
                if (heldUsed) return;
                heldUsed = true;
            }
            else heldUsed = false;
            if (interval > 0) {
                long d = System.currentTimeMillis() - last;
                if (d <= interval) return;
                last = System.currentTimeMillis();
            }
            history.push(new UndoStep<>(property, oldValue));
        }
    
        public <U> void auto(Property<U> property) {
            property.addListener((observable, oldValue, newValue) -> {
                submit(property, oldValue);
            });
        }
        
    }
    
}
