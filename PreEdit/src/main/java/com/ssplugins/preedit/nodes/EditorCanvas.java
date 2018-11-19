package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.edit.CanvasLayer;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.NodeModule;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.util.calc.ExpandableBounds;
import com.ssplugins.preedit.util.data.Range;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class EditorCanvas extends StackPane {
    
    private Pane posPane;
    private Pane bgPane;
    private ResizeHandle handle;
    
    private Canvas transparentLayer;
    private static Canvas debug;
    
    private ExpandableBounds viewport;
    
    private DoubleProperty scaleFactor;
    private Scale scale;
    private Range scaleRange;
    
    public EditorCanvas(double width, double height) {
        viewport = new ExpandableBounds(0, 0, width, height);
        this.prefWidthProperty().bind(this.minWidthProperty());
        this.prefHeightProperty().bind(this.minHeightProperty());
        this.minWidthProperty().bind(viewport.widthProperty());
        this.minHeightProperty().bind(viewport.heightProperty());
        transparentLayer = new Canvas(width, height);
        transparentLayer.widthProperty().bind(this.minWidthProperty());
        transparentLayer.heightProperty().bind(this.minHeightProperty());
        debug = new Canvas(width, height);
        debug.setMouseTransparent(true);
        setCanvasSize(width, height);
        bgPane = new Pane();
        this.getChildren().add(bgPane);
        posPane = new Pane();
        this.getChildren().add(posPane);
        handle = new ResizeHandle();
        handle.translateXProperty().bind(viewport.xProperty().negate());
        handle.translateYProperty().bind(viewport.yProperty().negate());
        posPane.getChildren().add(handle);
        posPane.getChildren().add(debug);
        bgPane.getChildren().add(transparentLayer);
    
        scaleFactor = new SimpleDoubleProperty(1);
        scaleRange = Range.from(0.1, 5);
        scale = new Scale(scaleFactor.get(), scaleFactor.get());
        scale.xProperty().bind(scaleFactor);
        scale.yProperty().bind(scaleFactor);
        DoubleBinding offset = this.widthProperty().multiply(scaleFactor.negate().add(1)).divide(2);
        this.translateXProperty().bind(offset);
        this.translateYProperty().bind(offset);
        this.getTransforms().addAll(scale);
    }
    
    public static Canvas debugCanvas() {
        return debug;
    }
    
    public static GraphicsContext debugContext() {
        return debugCanvas().getGraphicsContext2D();
    }
    
    public static void clearDebug() {
        debugContext().clearRect(0, 0, debug.getWidth(), debug.getHeight());
    }
    
    public static void rotate(GraphicsContext context, double cx, double cy, double deg) {
        if (deg == 0) return;
        context.translate(cx, cy);
        context.rotate(deg);
        context.translate(-cx, -cy);
    }
    
    public ExpandableBounds getViewport() {
        return viewport;
    }
    
    public double getScaleFactor() {
        return scaleFactor.get();
    }
    
    public DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }
    
    public Range getScaleRange() {
        return scaleRange;
    }
    
    public NodeHandle createNodeHandle() {
        return new NodeHandle(posPane);
    }
    
    public ResizeHandle getHandle() {
        return handle;
    }
    
    public ResizeHandle getHandleUnbound() {
        handle.unlink();
        handle.hide();
        handle.setDraggable(true);
        handle.hide();
        return getHandle();
    }
    
    public Canvas getTransparentLayer() {
        return transparentLayer;
    }
    
    public void setCanvasSize(double width, double height) {
//        this.setMinWidth(width);
//        this.setMinHeight(height);
        viewport.setOriginalWidth(width);
        viewport.setOriginalHeight(height);
        viewport.reset();
//        transparentLayer.setWidth(width);
//        transparentLayer.setHeight(height);
        debug.setWidth(width);
        debug.setHeight(height);
    }
    
    public void addLayer() {
        newLayer();
    }
    
    public void removeLayer() {
        this.getChildren().stream().filter(node -> node instanceof PaneCanvas).findFirst().ifPresent(node -> this.getChildren().remove(node));
    }
    
    public void setLayerCount(int layers) {
        long count = this.getChildren().stream().filter(node -> node instanceof PaneCanvas).count();
        long d = layers - count;
        if (d > 0) {
            LongStream.range(0, d).forEach(value -> this.addLayer());
        }
        else if (d < 0) {
            LongStream.range(0, d).forEach(value -> this.removeLayer());
        }
    }
    
    public void clearAll() {
        this.getChildren().stream().filter(node -> node instanceof PaneCanvas).forEach(node -> {
            ((PaneCanvas) node).clearNode();
            clear(((PaneCanvas) node).getCanvas());
        });
        clear(getTransparentLayer());
    }
    
    public void fillTransparent() {
        GraphicsContext gc = transparentLayer.getGraphicsContext2D();
        gc.clearRect(0, 0, transparentLayer.getWidth(), transparentLayer.getHeight());
        for (int x = 0; x < transparentLayer.getWidth(); x += 5) {
            for (int y = 0; y < transparentLayer.getHeight(); y += 5) {
                Color c = ((x + y) % 2 == 0 ? Color.WHITE : Color.LIGHTGRAY);
                gc.setFill(c);
                gc.fillRect(x, y, 5, 5);
            }
        }
    }
    
    public void renderImage(boolean display, List<Module> modules, boolean editor) throws SilentFailException {
        clearAll();
        viewport.reset();
        ListIterator<Module> it = modules.listIterator(modules.size());
        for (Node node : this.getChildren()) {
            if (!(node instanceof PaneCanvas)) continue;
            PaneCanvas paneCanvas = (PaneCanvas) node;
            paneCanvas.clearNode();
            if (!it.hasPrevious()) break;
            Module m = it.previous();
            if (paneCanvas.canvasLoaded()) {
                paneCanvas.getCanvas().setEffect(null);
                paneCanvas.getGraphics().save();
            }
            if (m instanceof NodeModule) {
                Node n = ((NodeModule) m).getNode();
                n.setEffect(null);
                paneCanvas.setNode(n);
                ((NodeModule) m).requestExpansion(viewport);
                renderEffects(m.getEffects(), paneCanvas, n, editor);
            }
            else {
                m.draw(paneCanvas, editor);
                renderEffects(m.getEffects(), paneCanvas, null, editor);
            }
            if (paneCanvas.canvasLoaded()) {
                paneCanvas.getGraphics().restore();
            }
        }
        if (display) fillTransparent();
        else handle.hide();
        
    }
    
    private void renderEffects(List<Effect> list, CanvasLayer canvas, Node node, boolean editor) throws SilentFailException {
        ListIterator<Effect> it = list.listIterator();
        while (it.hasNext()) {
            Effect e = it.next();
            e.reset();
            e.apply(canvas, node, editor);
        }
    }
    
    private PaneCanvas newLayer() {
        PaneCanvas c = new PaneCanvas(this.getMinWidth(), this.getMinHeight(), viewport);
        c.minWidthProperty().bind(this.minWidthProperty());
        c.minHeightProperty().bind(this.minHeightProperty());
        this.getChildren().add(this.getChildren().size() - 1, c);
        return c;
    }
    
    private List<Canvas> getLayers() {
        return this.getChildren().stream().filter(node -> node instanceof PaneCanvas).map(node -> ((PaneCanvas) node).getCanvas()).collect(Collectors.toList());
    }
    
    private void clear(Canvas canvas) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public class NodeHandle {
        private Pane pane;
        private boolean added;
        
        private Node node;
        
        private NodeHandle(Pane pane) {
            this.pane = pane;
        }
        
        public Node getNode() {
            return node;
        }
        
        public void setNode(Node node) {
            boolean a = added;
            if (a) remove();
            this.node = node;
            if (a) add();
        }
        
        public void toggle(boolean add) {
            if (add) add();
            else remove();
        }
        
        public void add() {
            if (!added) {
                if (node == null) return;
                added = true;
                pane.getChildren().add(node);
            }
        }
        
        public void remove() {
            if (added) {
                added = false;
                pane.getChildren().remove(node);
            }
        }
        
    }
    
}
