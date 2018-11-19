package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.util.UITools;
import com.ssplugins.preedit.util.calc.SizeHandler;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResizeHandle extends AnchorPane {
    
    public static final double HANDLE_SIZE = 8;
    public static final double HALF_HANDLE = HANDLE_SIZE / 2;
    
    private Pane topLeft, topRight, bottomLeft, bottomRight, top, right, bottom, left, rotate;
    private SizeHandler handler;
    
    private AtomicBoolean draggable = new AtomicBoolean(true);
    private Property<Number> x, y, width, height, angle;
    private ObservableValue<Bounds> boundsProperty;
    private ChangeListener<Bounds> boundsListener = getBoundsListener();
    public final static Border BORDER = UITools.border(Color.MAGENTA);
    public final static Border BORDER_ALT = UITools.border(Color.BLUE);
    
    public ResizeHandle() {
        this.setBorder(BORDER);
        topLeft = new Pane();
        setupAnchor(topLeft, Pos.TOP_LEFT, Cursor.NW_RESIZE, BORDER);
        topRight = new Pane();
        setupAnchor(topRight, Pos.TOP_RIGHT, Cursor.NE_RESIZE, BORDER);
        bottomLeft = new Pane();
        setupAnchor(bottomLeft, Pos.BOTTOM_LEFT, Cursor.SW_RESIZE, BORDER);
        bottomRight = new Pane();
        setupAnchor(bottomRight, Pos.BOTTOM_RIGHT, Cursor.SE_RESIZE, BORDER);
        top = new Pane();
        setupAnchor(top, Pos.TOP_CENTER, Cursor.N_RESIZE, null);
        right = new Pane();
        setupAnchor(right, Pos.CENTER_RIGHT, Cursor.E_RESIZE, null);
        bottom = new Pane();
        setupAnchor(bottom, Pos.BOTTOM_CENTER, Cursor.S_RESIZE, null);
        left = new Pane();
        setupAnchor(left, Pos.CENTER_LEFT, Cursor.W_RESIZE, null);
        rotate = new Pane();
        rotate.setId("resize_rotate");
        setupAnchor(rotate, Pos.TOP_RIGHT, Cursor.CROSSHAIR, UITools.border(Color.BLUE), 1);
        this.getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight, top, right, bottom, left, rotate);
        this.prefWidthProperty().bind(this.minWidthProperty());
        this.prefHeightProperty().bind(this.minHeightProperty());
        this.setCursor(Cursor.MOVE);
        handler = new SizeHandler();
        handler.outXProperty().bindBidirectional(this.layoutXProperty());
        handler.outYProperty().bindBidirectional(this.layoutYProperty());
        handler.outWidthProperty().bindBidirectional(this.minWidthProperty());
        handler.outHeightProperty().bindBidirectional(this.minHeightProperty());
        handler.outAngleProperty().bindBidirectional(this.rotateProperty());
        makeDraggable();
        hide();
    }
    
    public static void setupAnchor(Pane pane, Pos anchor, Cursor cursor, Border border) {
        setupAnchor(pane, anchor, cursor, border, 0);
    }
    
    public static void setupAnchor(Pane pane, Pos anchor, Cursor cursor, Border border, int offset) {
        pane.setMinWidth(HANDLE_SIZE);
        pane.setMinHeight(HANDLE_SIZE);
        if (border != null) pane.setBorder(border);
        if (cursor != null) pane.setCursor(cursor);
        double factor = (HANDLE_SIZE * offset + HALF_HANDLE);
        positionAnchor(pane, anchor, factor, factor);
    }
    
    public static void positionAnchor(Pane pane, Pos anchor, double x, double y) {
        switch (anchor) {
            case TOP_CENTER:
                AnchorPane.setTopAnchor(pane, -y);
                AnchorPane.setLeftAnchor(pane, x);
                AnchorPane.setRightAnchor(pane, x);
                break;
            case TOP_LEFT:
                AnchorPane.setTopAnchor(pane, -y);
                AnchorPane.setLeftAnchor(pane, -x);
                break;
            case TOP_RIGHT:
                AnchorPane.setTopAnchor(pane, -y);
                AnchorPane.setRightAnchor(pane, -x);
                break;
            case CENTER_LEFT:
                AnchorPane.setLeftAnchor(pane, -x);
                AnchorPane.setTopAnchor(pane, y);
                AnchorPane.setBottomAnchor(pane, y);
                break;
            case CENTER_RIGHT:
                AnchorPane.setRightAnchor(pane, -x);
                AnchorPane.setTopAnchor(pane, y);
                AnchorPane.setBottomAnchor(pane, y);
                break;
            case BOTTOM_LEFT:
                AnchorPane.setBottomAnchor(pane, -y);
                AnchorPane.setLeftAnchor(pane, -x);
                break;
            case BOTTOM_CENTER:
                AnchorPane.setBottomAnchor(pane, -y);
                AnchorPane.setLeftAnchor(pane, x);
                AnchorPane.setRightAnchor(pane, x);
                break;
            case BOTTOM_RIGHT:
                AnchorPane.setBottomAnchor(pane, -y);
                AnchorPane.setRightAnchor(pane, -x);
                break;
            default:
                break;
        }
    }
    
    public void setSizeable(boolean sizeable) {
        this.getChildren().forEach(node -> {
            if (node.getId() == null) {
                node.setVisible(sizeable);
            }
        });
    }
    
    public void setSpinnable(boolean spinnable) {
        this.getChildren().forEach(node -> {
            if (node.getId() != null) {
                node.setVisible(spinnable);
            }
        });
    }
    
    public void setDraggable(boolean draggable) {
        this.draggable.set(draggable);
        if (draggable) this.setCursor(Cursor.MOVE);
        else this.setCursor(Cursor.DEFAULT);
    }
    
    private ChangeListener<Bounds> getBoundsListener() {
        return (observable, oldValue, newValue) -> {
            this.minWidthProperty().set(newValue.getWidth());
            this.minHeightProperty().set(newValue.getHeight());
        };
    }
    
    public void hide() {
        this.setVisible(false);
    }
    
    public void show() {
        this.setVisible(true);
    }
    
    private void unbind(Property<Number> field, Property<Number> property) {
        if (field == null) return;
        field.unbindBidirectional(property);
    }
    
    private void makeDraggable() {
        EventHandler<MouseEvent> clickEvent = event -> {
            Optional<Pos> op = findPos(event.getSource());
            op.ifPresent(pos -> {
                if (!draggable.get()) return;
                handler.begin(toCanvas(event.getSceneX(), event.getSceneY()), pos);
            });
        };
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, clickEvent);
        this.getChildren().forEach(node -> {
            if (node.getId() == null) {
                node.addEventFilter(MouseEvent.MOUSE_PRESSED, clickEvent);
            }
            else {
                node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!draggable.get()) return;
                    handler.beginRotate(toCanvas(event.getSceneX(), event.getSceneY()));
                });
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (!draggable.get()) return;
            handler.update(toCanvas(event.getSceneX(), event.getSceneY()), event);
        });
    }
    
    private Point2D toCanvas(double x, double y) {
        return this.getParent().sceneToLocal(x, y);
    }
    
    private Optional<Pos> findPos(Object src) {
        if (src == this) return Optional.of(Pos.CENTER);
        else if (src == topLeft) return Optional.of(Pos.TOP_LEFT);
        else if (src == top) return Optional.of(Pos.TOP_CENTER);
        else if (src == topRight) return Optional.of(Pos.TOP_RIGHT);
        else if (src == left) return Optional.of(Pos.CENTER_LEFT);
        else if (src == right) return Optional.of(Pos.CENTER_RIGHT);
        else if (src == bottomLeft) return Optional.of(Pos.BOTTOM_LEFT);
        else if (src == bottom) return Optional.of(Pos.BOTTOM_CENTER);
        else if (src == bottomRight) return Optional.of(Pos.BOTTOM_RIGHT);
        return Optional.empty();
    }
    
    public void link(LocationInput input) {
        show();
        setSizeable(input.isSizeable());
        setSpinnable(input.canSpin());
        if (input.isGeneratorMode() && !input.isUserProvided()) {
            setDraggable(false);
            setSizeable(false);
            setSpinnable(false);
        }
        link(HandleProperty.X, input.xProperty());
        link(HandleProperty.Y, input.yProperty());
        link(HandleProperty.WIDTH, input.widthProperty());
        link(HandleProperty.HEIGHT, input.heightProperty());
        link(HandleProperty.ANGLE, input.angleProperty());
    }
    
    public void link(ObservableValue<Bounds> property) {
        show();
        boundsProperty = property;
        property.addListener(boundsListener);
        this.minWidthProperty().set(property.getValue().getWidth());
        this.minHeightProperty().set(property.getValue().getHeight());
    }
    
    public void link(HandleProperty property, Property<Number> numberProperty) {
        show();
        switch (property) {
            case X:
                unbind(x, this.layoutXProperty());
                x = numberProperty;
                this.layoutXProperty().bindBidirectional(numberProperty);
                break;
            case Y:
                unbind(y, this.layoutYProperty());
                y = numberProperty;
                this.layoutYProperty().bindBidirectional(numberProperty);
                break;
            case WIDTH:
                unbind(width, this.minWidthProperty());
                width = numberProperty;
                this.minWidthProperty().bindBidirectional(numberProperty);
                break;
            case HEIGHT:
                unbind(height, this.minHeightProperty());
                height = numberProperty;
                this.minHeightProperty().bindBidirectional(numberProperty);
                break;
            case ANGLE:
                unbind(angle, this.rotateProperty());
                angle = numberProperty;
                this.rotateProperty().bindBidirectional(numberProperty);
                break;
        }
    }
    
    public void unlink() {
        unbind(x, this.layoutXProperty());
        unbind(y, this.layoutYProperty());
        unbind(width, this.minWidthProperty());
        unbind(height, this.minHeightProperty());
        unbind(angle, this.rotateProperty());
        if (boundsProperty != null) {
            boundsProperty.removeListener(boundsListener);
            boundsProperty = null;
        }
    }
    
    public enum HandleProperty {
        X, Y, WIDTH, HEIGHT, ANGLE;
    }
    
}
