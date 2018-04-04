package com.ssplugins.preedit.util.wrapper;

import javafx.beans.property.SimpleObjectProperty;

import java.util.Optional;
import java.util.function.Function;

public class FilteredObjectProperty<T> extends SimpleObjectProperty<T> {
	
	private Function<T, Optional<T>> filter;
	
	public FilteredObjectProperty(Function<T, Optional<T>> filter) {
		this.filter = filter;
	}
	
	public FilteredObjectProperty(T initialValue, Function<T, Optional<T>> filter) {
		super(initialValue);
		this.filter = filter;
	}
	
	@Override
	public void set(T newValue) {
		if (filter != null) {
			Optional<T> op = filter.apply(newValue);
			if (op.isPresent()) newValue = op.get();
			else return;
		}
		super.set(newValue);
	}
	
	@Override
	public void setValue(T v) {
		set(v);
	}
	
	
	
}
