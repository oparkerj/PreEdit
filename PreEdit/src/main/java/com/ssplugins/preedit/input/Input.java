package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.nodes.UserInput;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.Util;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Input<N extends Node, O> implements Comparable<Input> {
    
    private N node;
    private UserInput<N> displayNode;
    private JsonConverter<O> converter;
    private boolean ready, gen, init;
    private int order = -1;
    private BooleanProperty userProvided = new SimpleBooleanProperty(false);
    private Runnable update;
    
    protected abstract N createInputNode();
    
    protected abstract O getNodeValue(N node) throws InvalidInputException;
    
    protected abstract void setNodeValue(N node, O value);
    
    protected abstract boolean isValid(O value);
    
    protected abstract JsonConverter<O> getJsonConverter();
    
    protected abstract void setUpdateTrigger(N node, Runnable update);
    
    protected final void ready() {
        this.ready = true;
        this.node = createInputNode();
        if (node != null) {
            displayNode = new UserInput<>(node);
            displayNode.getCheckBox().selectedProperty().bindBidirectional(userProvided);
            displayNode.getCheckBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (update != null) update.run();
            });
        }
        this.converter = getJsonConverter();
        getValue();
    }
    
    public final int getOrder() {
        return order;
    }
    
    public final void setOrder(int order) {
        this.order = order;
    }
    
    public final boolean isReady() {
        return ready;
    }
    
    public final boolean isInit() {
        return init;
    }
    
    protected final void setSlideAction(UserInput.SlideAction<N> action, Function<N, Double> function) {
        displayNode.setSlideAction(action, function);
    }
    
    public final void setGeneratorMode() {
        gen = true;
        if (displayNode != null) {
            displayNode.getCheckBox().setVisible(false);
        }
    }
    
    public final boolean isGeneratorMode() {
        return gen;
    }
    
    public final void setUserProvided(boolean userProvided) {
        this.userProvided.set(userProvided);
    }
    
    public final boolean isUserProvided() {
        return userProvided.get();
    }
    
    public final void setProvidedVisible(boolean visible) {
        if (displayNode != null) {
            displayNode.getCheckBox().setVisible(visible);
        }
    }
    
    public final void setDisabled(boolean disabled) {
        if (displayNode != null) {
            displayNode.setDisable(disabled);
        }
    }
    
    public final void setUpdateTrigger(Runnable update) {
        this.update = update;
        setUpdateTrigger(node, update);
    }
    
    public final <T extends Input> Optional<T> as(Class<T> type) {
        if (type.isAssignableFrom(this.getClass())) return Optional.of(type.cast(this));
        return Optional.empty();
    }
    
    public final void note(String note) {
        note(note, null);
    }
    
    public final void note(String note, Consumer<Label> label) {
        if (!Platform.isFxApplicationThread()) {
            String finalNote = note;
            Util.runFXSafe(() -> note(finalNote, label));
            return;
        }
        if (displayNode != null) {
            if (note == null) note = "";
            displayNode.getNote().setText(note);
            if (label != null) label.accept(displayNode.getNote());
        }
    }
    
    public final N getNode() {
        return node;
    }
    
    public final UserInput<N> getDisplayNode() {
        return displayNode;
    }
    
    public final Optional<O> getValue() {
        try {
            O o = getNodeValue(node);
            if (isValid(o)) {
                setValid(true);
                return Optional.ofNullable(o);
            }
            setValid(false);
            return Optional.empty();
        } catch (InvalidInputException e) {
            setValid(false);
            return Optional.empty();
        }
    }
    
    public final void setValue(O o) {
        setNodeValue(node, o);
    }
    
    public final void init(O o) {
        init = true;
        setValue(o);
        init = false;
    }
    
    public final JsonElement serialize() {
        return getValue().map(converter::toJson).orElse(JsonNull.INSTANCE);
    }
    
    public final void deserialize(JsonElement value) {
        init(converter.fromJson(value));
    }
    
    public final boolean isValid() {
        return getValue().isPresent();
    }
    
    private void setValid(boolean valid) {
        if (displayNode != null) displayNode.setValid(valid);
    }
    
    @Override
    public final int compareTo(Input o) {
        return Integer.compare(order, o.order);
    }
}
