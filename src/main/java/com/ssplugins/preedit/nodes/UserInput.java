package com.ssplugins.preedit.nodes;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class UserInput<N extends Node> extends GridPane {
	
	private static final Border BORDER = new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	
	private Label label;
	private Label note;
	private CheckBox userProvided;
	private N inputNode;
	
	public UserInput(N inputNode) {
		this.inputNode = inputNode;
		label = new Label();
        note = new Label();
        note.setWrapText(true);
		userProvided = new CheckBox("Provided");
		userProvided.setAllowIndeterminate(false);
		userProvided.setSelected(false);
		this.disabledProperty().addListener((observable, oldValue, newValue) -> {
		    if (newValue) userProvided.setSelected(false);
        });
		//
		this.setBorder(BORDER);
		this.setPadding(new Insets(10));
		this.setHgap(5);
		this.setVgap(5);
		this.add(label, 0, 0);
		this.add(userProvided, 1, 0);
		this.add(inputNode, 0, 1, 2, 1);
	}
	
	public interface SlideAction<T> {
		void onSlide(T node, double initial, double dx);
	}
	
	public void setSlideAction(SlideAction<N> action, Function<N, Double> function) {
		AtomicReference<Double> sx = new AtomicReference<>((double) 0);
		AtomicReference<Double> si = new AtomicReference<>((double) 0);
		label.setOnMousePressed(event -> {
			sx.set(event.getSceneX());
			si.set(function.apply(inputNode));
		});
		label.setOnMouseDragged(event -> {
			double dx = event.getSceneX() - sx.get();
			action.onSlide(inputNode, si.get(), dx);
		});
		label.setCursor(Cursor.E_RESIZE);
	}
	
	public void setValid(boolean valid) {
		if (valid) label.setTextFill(Color.BLACK);
		else label.setTextFill(Color.RED);
	}
	
	public void update(String name) {
		label.setText(name + ":");
	}
	
	public Label getLabel() {
		return label;
	}
	
	public CheckBox getCheckBox() {
		return userProvided;
	}
	
	public Node getInputNode() {
		return inputNode;
	}
	
	public Label getNote() {
	    return note;
    }
	
}
