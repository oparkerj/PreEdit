package com.ssplugins.meme.input;

import com.ssplugins.meme.exceptions.InvalidInputException;
import javafx.scene.Node;

import java.util.Optional;

public abstract class Input<N extends Node, O> {
	
	private N node;
	private boolean ready;

	protected abstract N createInputNode();
	
	protected abstract O getNodeValue(N node) throws InvalidInputException;
	
	protected abstract boolean isValid(O value);
	
	protected final void ready() {
		this.ready = true;
		this.node = createInputNode();
	}
	
	public final boolean isReady() {
		return ready;
	}
	
	public final <T extends Input> Optional<T> as(Class<T> type) {
		if (type.isAssignableFrom(this.getClass())) return Optional.of(type.cast(this));
		return Optional.empty();
	}
	
	public final N getNode() {
		return node;
	}
	
	public final O getValue() throws InvalidInputException {
		return getNodeValue(node);
	}
	
	public final boolean isValid() {
		try {
			return isValid(getValue());
		} catch (InvalidInputException e) {
			return false;
		}
	}

}
