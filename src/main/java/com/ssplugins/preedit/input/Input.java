package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.JsonConverter;
import javafx.scene.Node;

import java.util.Optional;

public abstract class Input<N extends Node, O> {
	
	private N node;
	private JsonConverter<O> converter;
	private boolean ready;
	private boolean userProvided;

	protected abstract N createInputNode();
	
	protected abstract O getNodeValue(N node) throws InvalidInputException;
	
	protected abstract void setNodeValue(N node, O value);
	
	protected abstract boolean isValid(O value);
	
	protected abstract JsonConverter<O> getJsonConverter();
	
	protected final void ready() {
		this.ready = true;
		this.node = createInputNode();
		this.converter = getJsonConverter();
	}
	
	public final boolean isReady() {
		return ready;
	}
	
	public final void setUserProvided(boolean userProvided) {
		this.userProvided = userProvided;
	}
	
	public final boolean isUserProvided() {
		return userProvided;
	}
	
	public final <T extends Input> Optional<T> as(Class<T> type) {
		if (type.isAssignableFrom(this.getClass())) return Optional.of(type.cast(this));
		return Optional.empty();
	}
	
	public final N getNode() {
		return node;
	}
	
	public final Optional<O> getValue() {
		try {
			O o = getNodeValue(node);
			if (isValid(o)) return Optional.ofNullable(o);
			return Optional.empty();
		} catch (InvalidInputException e) {
			return Optional.empty();
		}
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

}
