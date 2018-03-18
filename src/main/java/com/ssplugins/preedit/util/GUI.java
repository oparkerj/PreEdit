package com.ssplugins.preedit.util;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GUI extends Scene {
	
	private GridPane pane;
	private String title;
	private Map<String, Node> nodes = new HashMap<>();
	
	public GUI(String title) {
		super(new GridPane());
		pane = (GridPane) this.getRoot();
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public GridPane getPane() {
		return pane;
	}
	
	public void setPadding(Insets padding) {
		pane.setPadding(padding);
	}
	
	public void setPadding(int pad) {
		setPadding(new Insets(pad, pad, pad, pad));
	}
	
	public void setGaps(int gap) {
		pane.setHgap(gap);
		pane.setVgap(gap);
	}
	
	public <T extends Node> void add(String id, int row, int col, Supplier<T> node, Consumer<T> edit) {
		add(id, row, col, 1, 1, node, edit);
	}
	
	public <T extends Node> void add(String id, int row, int col, int rowSpan, int colSpan, Supplier<T> node, Consumer<T> edit) {
		T n = node.get();
		if (edit != null) edit.accept(n);
		pane.add(n, col, row, colSpan, rowSpan);
		nodes.put(id, n);
	}
	
	public void add(String id, int row, int col, Node node) {
		add(id, row, col, 1, 1, node);
	}
	
	public void add(String id, int row, int col, int rowSpan, int colSpan, Node node) {
		pane.add(node, col, row, colSpan, rowSpan);
		if (id != null) nodes.put(id, node);
	}
	
	public <T extends Node> Optional<T> get(String id, Class<T> type) {
		 return Optional.ofNullable(nodes.get(id)).filter(node -> type.isAssignableFrom(node.getClass())).map(type::cast);
	}
	
	public void hide(String id) {
		get(id, Node.class).ifPresent(node -> node.setVisible(false));
	}
	
	public void show(String id) {
		get(id, Node.class).ifPresent(node -> node.setVisible(true));
	}
	
}
