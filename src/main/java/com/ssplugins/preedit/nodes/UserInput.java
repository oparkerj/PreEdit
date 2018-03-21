package com.ssplugins.preedit.nodes;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class UserInput extends GridPane {
	
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
		this.add(label, 0, 0);
		this.add(userProvided, 1, 0);
		this.add(inputNode, 0, 1, 2, 1);
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
