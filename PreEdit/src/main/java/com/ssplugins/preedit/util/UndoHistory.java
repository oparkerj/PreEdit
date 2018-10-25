package com.ssplugins.preedit.util;

import javafx.beans.property.Property;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UndoHistory {
    
    private int capacity = 200;
    private int pointer = -1;
    private AtomicBoolean working;
    
    private List<UndoTrigger> triggers = new ArrayList<>();
    private List<Undo> list = new ArrayList<>();
    
    private boolean mouseDown;
    private List<KeyCode> keys = new ArrayList<>();
    
    private Runnable onUpdate;
    
    public UndoHistory(Stage stage) {
        working = new AtomicBoolean();
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            mouseDown = true;
        });
        stage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            mouseDown = false;
            collectSteps();
        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            keys.add(event.getCode());
        });
        stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            keys.remove(event.getCode());
            collectSteps();
        });
    }
    
    private void collectSteps() {
        List<Undo> steps = new ArrayList<>();
        triggers.stream().map(UndoTrigger::getPendingChange).filter(Optional::isPresent).map(Optional::get).forEach(steps::add);
        if (steps.size() == 0) return;
        if (steps.size() == 1) {
            push(steps.get(0));
        }
        else {
            push(new UndoGroup(steps));
        }
    }
    
    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }
    
    private void update() {
        if (onUpdate != null) {
            onUpdate.run();
        }
    }
    
    public boolean isButtonHeld() {
        if (mouseDown) return true;
        if (keys.contains(KeyCode.UP)) return true;
        if (keys.contains(KeyCode.DOWN)) return true;
        if (keys.contains(KeyCode.LEFT)) return true;
        return keys.contains(KeyCode.RIGHT);
    }
    
    public boolean isWorking() {
        return working.get();
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
    
    public UndoTrigger createTrigger() {
        UndoTrigger trigger = new UndoTrigger(this);
        triggers.add(trigger);
        return trigger;
    }
    
    public void undo() {
        synchronized (this) {
            if (list.size() == 0) return;
            if (working.get()) return;
            working.set(true);
            boolean push = false;
            if (pointer == -1) {
                push = true;
                pointer = list.size();
            }
            pointer--;
            if (pointer < 0) {
                pointer = 0;
                working.set(false);
                return;
            }
            Undo step = list.get(pointer);
            if (push) list.add(step.redoStep());
            step.restore();
            working.set(false);
            update();
        }
    }
    
    public void redo() {
        synchronized (this) {
            if (working.get()) return;
            working.set(true);
            if (pointer == -1) return;
            pointer++;
            if (pointer >= list.size()) {
                pointer = list.size() - 1;
                working.set(false);
                return;
            }
            Undo step = list.get(pointer);
            step.restore();
            working.set(false);
            update();
        }
    }
    
    private void push(Undo step) {
        if (pointer > -1) {
            pointer++;
            while (list.size() > pointer) list.remove(pointer);
            pointer = -1;
        }
        list.add(step);
        while (list.size() > capacity) list.remove(0);
    }
    
    private abstract class Undo {
        
        public abstract void restore();
        
        public abstract Undo redoStep();
        
    }
    
    private class UndoStep<T> extends Undo {
        
        private Property<T> property;
        private T oldValue;
        
        private UndoStep(Property<T> property, T oldValue) {
            this.property = property;
            this.oldValue = oldValue;
        }
        
        @Override
        public Undo redoStep() {
            return new UndoStep<>(property, property.getValue());
        }
        
        @Override
        public void restore() {
            property.setValue(oldValue);
        }
        
    }
    
    private class UndoGroup extends Undo {
    
        private List<Undo> steps;
    
        public UndoGroup() {
            steps = new ArrayList<>();
        }
    
        public UndoGroup(List<Undo> steps) {
            this.steps = steps;
        }
    
        public void addStep(Undo step) {
            steps.add(step);
        }
    
        @Override
        public Undo redoStep() {
            UndoGroup redo = new UndoGroup();
            steps.forEach(step -> redo.addStep(step.redoStep()));
            return redo;
        }
    
        @Override
        public void restore() {
            steps.forEach(Undo::restore);
        }
        
    }
    
    public class UndoTrigger {
    
        private UndoHistory history;
    
        private AtomicReference<Undo> submit = new AtomicReference<>();
    
        public UndoTrigger(UndoHistory history) {
            this.history = history;
        }
        
        private Optional<Undo> getPendingChange() {
            Undo step = submit.get();
            submit.set(null);
            return Optional.ofNullable(step);
        }
    
        public <U> void submit(Property<U> property, U oldValue) {
            if (history.isWorking()) return;
            if (history.isButtonHeld()) {
                if (submit.get() != null) return;
                submit.updateAndGet(undoStep -> new UndoStep<>(property, oldValue));
                return;
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
