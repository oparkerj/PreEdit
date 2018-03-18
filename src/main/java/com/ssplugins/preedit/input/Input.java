package com.ssplugins.preedit.input;

import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.exceptions.SilentFailException;
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
	
	public final Optional<O> getValue() {
		try {
			O o = getNodeValue(node);
			if (isValid(o)) return Optional.ofNullable(o);
			return Optional.empty();
		} catch (InvalidInputException e) {
			return Optional.empty();
		}
	}
	
	public final boolean isValid() {
		return getValue().isPresent();
	}

}
