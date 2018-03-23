package com.ssplugins.preedit.util;

import com.ssplugins.preedit.edit.Template;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.atomic.AtomicBoolean;

public class State {
	
	private AtomicBoolean rendering = new AtomicBoolean(false);
	private Runnable renderCall;
	
	private BooleanProperty upToDate = new SimpleBooleanProperty(true);
	private BooleanProperty saved = new SimpleBooleanProperty(true);
	private ObjectProperty<Template> template = new SimpleObjectProperty<>(null);
	
	public State() {
		upToDate.addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				saved.set(false);
				if (!rendering.get() && renderCall != null) {
					rendering.set(true);
					renderCall.run();
					rendering.set(false);
					upToDate.set(true);
				}
			}
		});
	}
	
	public void render() {
		upToDate.set(false);
	}
	
	public void setRenderCall(Runnable renderCall) {
		this.renderCall = renderCall;
	}
	
	public boolean isUpToDate() {
		return upToDate.get();
	}
	
	public BooleanProperty upToDateProperty() {
		return upToDate;
	}
	
	public boolean isSaved() {
		return saved.get();
	}
	
	public BooleanProperty savedProperty() {
		return saved;
	}
	
	public Template getTemplate() {
		return template.get();
	}
	
	public ObjectProperty<Template> templateProperty() {
		return template;
	}
}
