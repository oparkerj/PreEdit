package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.nodes.UserInput;
import com.ssplugins.preedit.util.JsonConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;

import java.util.Optional;
import java.util.function.Function;

public abstract class Input<N extends Node, O> {
	
	private N node;
	private UserInput<N> displayNode;
	private JsonConverter<O> converter;
	private boolean ready, gen;
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
	
	public final boolean isReady() {
		return ready;
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
	
	public final void setUpdateTrigger(Runnable update) {
		this.update = update;
		setUpdateTrigger(node, update);
	}
	
	public final <T extends Input> Optional<T> as(Class<T> type) {
		if (type.isAssignableFrom(this.getClass())) return Optional.of(type.cast(this));
		return Optional.empty();
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
	
	public final JsonElement serialize() {
		return getValue().map(converter::toJson).orElse(JsonNull.INSTANCE);
	}
	
	public final void deserialize(JsonElement value) {
		setNodeValue(node, converter.fromJson(value));
	}
	
	public final boolean isValid() {
		return getValue().isPresent();
	}
	
	private void setValid(boolean valid) {
		if (displayNode != null) displayNode.setValid(valid);
	}

}
