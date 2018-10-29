package com.ssplugins.preedit.nodes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class PaneCanvas extends Pane {
    
    private Canvas canvas;
    private ObjectProperty<Node> node;
    
    private Rectangle clip;
    private IntegerProperty clipX, clipY, clipWidth, clipHeight;
    
    public PaneCanvas(double width, double height) {
        this.prefWidthProperty().bind(this.minWidthProperty());
        this.prefHeightProperty().bind(this.minHeightProperty());
        canvas = new Canvas(width, height);
        canvas.widthProperty().bind(this.minWidthProperty());
        canvas.heightProperty().bind(this.minHeightProperty());
        clip = new Rectangle(width, height);
        clipX = new SimpleIntegerProperty();
        clipY = new SimpleIntegerProperty();
        clipWidth = new SimpleIntegerProperty((int) this.getWidth());
        clipHeight = new SimpleIntegerProperty((int) this.getHeight());
        clip.xProperty().bind(this.clipX);
        clip.yProperty().bind(this.clipY);
        clip.widthProperty().bind(clipWidth);
        clip.heightProperty().bind(clipHeight);
        this.setClip(clip);
        this.getChildren().add(canvas);
        node = new SimpleObjectProperty<>();
        node.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                this.getChildren().remove(oldValue);
            }
            if (newValue != null) {
                this.getChildren().add(newValue);
            }
        });
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
    public Node getNode() {
        return node.get();
    }
    
    public ObjectProperty<Node> nodeProperty() {
        return node;
    }
    
    public void setNode(Node node) {
        this.node.set(node);
    }
    
    public void clearNode() {
        setNode(null);
    }
    
}
