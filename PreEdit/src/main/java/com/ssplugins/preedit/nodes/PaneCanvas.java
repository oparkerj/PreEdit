package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.edit.CanvasLayer;
import com.ssplugins.preedit.util.calc.ExpandableBounds;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class PaneCanvas extends Pane implements CanvasLayer {
    
    private ExpandableBounds viewport;
    
    private Canvas canvas;
    private ObjectProperty<Node> node;
    
    private Rectangle clip;
    
    public PaneCanvas(double width, double height, ExpandableBounds viewport) {
        this.viewport = viewport;
        this.translateXProperty().bind(viewport.xProperty().negate());
        this.translateYProperty().bind(viewport.yProperty().negate());
        this.prefWidthProperty().bind(this.minWidthProperty());
        this.prefHeightProperty().bind(this.minHeightProperty());
        clip = new Rectangle(width, height);
        clip.xProperty().bind(viewport.xProperty());
        clip.yProperty().bind(viewport.yProperty());
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        this.setClip(clip);
//        this.getChildren().add(canvas);
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
    
    @Override
    public boolean canvasLoaded() {
        return canvas != null;
    }
    
    @Override
    public Canvas getCanvas() {
        if (canvas == null) {
            canvas = new Canvas(this.getMinWidth(), this.getMinHeight());
            canvas.widthProperty().bind(this.minWidthProperty());
            canvas.heightProperty().bind(this.minHeightProperty());
        }
        return canvas;
    }
    
    @Override
    public GraphicsContext getGraphics() {
        return getCanvas().getGraphicsContext2D();
    }
    
    @Override
    public ExpandableBounds getExpandableBounds() {
        return viewport;
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
