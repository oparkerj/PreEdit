package com.ssplugins.preedit.util.wrapper;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GridMap extends GridPane {
    
    private Map<String, Node> nodes = new HashMap<>();
    
    public void add(String id, int row, int col, Node node) {
        add(id, row, col, 1, 1, node);
    }
    
    public void add(String id, int row, int col, int rowSpan, int colSpan, Node node) {
        super.add(node, col, row, colSpan, rowSpan);
        if (id != null) nodes.put(id, node);
    }
    
    public <T extends Node> Optional<T> get(String id, Class<T> type) {
        return Optional.ofNullable(nodes.get(id)).filter(node -> type.isAssignableFrom(node.getClass())).map(type::cast);
    }
    
}
