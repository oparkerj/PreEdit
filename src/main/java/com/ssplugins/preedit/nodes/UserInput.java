package com.ssplugins.preedit.nodes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class UserInput extends GridPane {
	
	private static final Border BORDER = new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	
	private Label label;
	private CheckBox userProvided;
	private Node inputNode;
	
	public UserInput(Node inputNode) {
		this.inputNode = inputNode;
		label = new Label();
		userProvided = new CheckBox("Provided");
		userProvided.setAllowIndeterminate(false);
		userProvided.setSelected(false);
		//
		this.setBorder(BORDER);
		this.setPadding(new Insets(10));
		this.setHgap(5);
		this.setVgap(5);
		this.add(label, 0, 0);
		this.add(userProvided, 1, 0);
		this.add(inputNode, 0, 1, 2, 1);
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
	
}
