package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.util.wrapper.OptionalProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Optional;
import java.util.function.Function;

public class FreesizeHandle extends AnchorPane {
    
    public static final double HANDLE_SIZE = ResizeHandle.HANDLE_SIZE;
    public static final double HALF_HANDLE = ResizeHandle.HALF_HANDLE;
    private static final Border BORDER = ResizeHandle.BORDER_ALT;
    
    private Pane topLeft, topRight, bottomLeft, bottomRight;
    private Line top, right, bottom, left;
    private Point ul, ur, ll, lr;
    private Point oul, our, oll, olr;
    
    private OptionalProperty<Bounds> bounds;
    private double startX, startY, px, py;
    private Point target;
    
    public FreesizeHandle() {
        topLeft = new Pane();
        setupHandle(topLeft);
        topRight = new Pane();
        setupHandle(topRight);
        bottomLeft = new Pane();
        setupHandle(bottomLeft);
        bottomRight = new Pane();
        setupHandle(bottomRight);
        top = new Line();
        setupLine(top);
        right = new Line();
        setupLine(right);
        bottom = new Line();
        setupLine(bottom);
        left = new Line();
        setupLine(left);
        this.getChildren().addAll(topLeft, topRight, bottomRight, bottomLeft, top, right, bottom, left);
        ul = new Point();
        ur = new Point();
        ll = new Point();
        lr = new Point();
        oul = new Point();
        our = new Point();
        oll = new Point();
        olr = new Point();
        pointBind(ul.xProperty(), Bounds::getMinX, oul.xProperty(), top.startXProperty(), left.endXProperty());
        pointBind(ul.yProperty(), Bounds::getMinY, oul.yProperty(), top.startYProperty(), left.endYProperty());
        pointBind(ur.xProperty(), Bounds::getMaxX, our.xProperty(), top.endXProperty(), right.startXProperty());
        pointBind(ur.yProperty(), Bounds::getMinY, our.yProperty(), top.endYProperty(), right.startYProperty());
        pointBind(ll.xProperty(), Bounds::getMinX, oll.xProperty(), bottom.endXProperty(), left.startXProperty());
        pointBind(ll.yProperty(), Bounds::getMaxY, oll.yProperty(), bottom.endYProperty(), left.startYProperty());
        pointBind(lr.xProperty(), Bounds::getMaxX, olr.xProperty(), bottom.startXProperty(), right.endXProperty());
        pointBind(lr.yProperty(), Bounds::getMaxY, olr.yProperty(), bottom.startYProperty(), right.endYProperty());
        oul.addListener(PointUpdate.paneLayout(topLeft));
        our.addListener(PointUpdate.paneLayout(topRight));
        oll.addListener(PointUpdate.paneLayout(bottomLeft));
        olr.addListener(PointUpdate.paneLayout(bottomRight));
        bounds = new OptionalProperty<>();
        bounds.addListener((observable, oldValue, newValue) -> {
            ul.update();
            ur.update();
            ll.update();
            lr.update();
        });
        makeDraggable();
    }
    
    private void setupHandle(Pane pane) {
        pane.setMinSize(HANDLE_SIZE, HANDLE_SIZE);
        pane.setBorder(BORDER);
        pane.setCursor(Cursor.MOVE);
    }
    
    private void setupLine(Line line) {
        line.setStrokeWidth(1);
        line.setStroke(Color.BLUE);
        line.setMouseTransparent(true);
    }
    
    private void pointBind(Property<Number> input, Function<Bounds, Double> function, Property<Number> output, Property<Number> l1, Property<Number> l2) {
        input.addListener((observable, oldValue, newValue) -> {
            double n = newValue.doubleValue();
            n += bounds.optional().map(function).orElse((double) 0);
            output.setValue(n);
            l1.setValue(n);
            l2.setValue(n);
        });
    }
    
    private void multiBind(Property<Number> property, Property<Number> o1, Property<Number> o2) {
        o1.bind(property);
        o2.bind(property);
    }
    
    // Draggable stuff
    private void makeDraggable() {
        EventHandler<MouseEvent> clickEvent = event -> {
            Optional<Point> op = getPane(event.getSource());
            op.ifPresent(point -> {
                target = point;
                Point2D p = toCanvas(event.getSceneX(), event.getSceneY());
                startX = p.getX();
                startY = p.getY();
                px = point.getX();
                py = point.getY();
            });
        };
        EventHandler<MouseEvent> moveEvent = event -> {
            if (target == null) return;
            Point2D p = toCanvas(event.getSceneX(), event.getSceneY());
            double dx = p.getX() - startX;
            double dy = p.getY() - startY;
            target.setX(px + dx);
            target.setY(py + dy);
        };
        this.getChildren().forEach(node -> {
            if (!(node instanceof Pane)) return;
            node.addEventFilter(MouseEvent.MOUSE_PRESSED, clickEvent);
        });
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, moveEvent);
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            target = null;
        });
    }
    
    private Point2D toCanvas(double x, double y) {
        return this.getParent().sceneToLocal(x, y);
    }
    
    private Optional<Point> getPane(Object src) {
        if (src == topLeft) return Optional.of(ul);
        else if (src == topRight) return Optional.of(ur);
        else if (src == bottomLeft) return Optional.of(ll);
        else if (src == bottomRight) return Optional.of(lr);
        return Optional.empty();
    }
    
    public Bounds getBounds() {
        return bounds.get();
    }
    
    public ObjectProperty<Bounds> boundsProperty() {
        return bounds;
    }
    
    public void setBounds(Bounds bounds) {
        this.bounds.set(bounds);
    }
    
    public Point getUl() {
        return ul;
    }
    
    public Point getUr() {
        return ur;
    }
    
    public Point getLl() {
        return ll;
    }
    
    public Point getLr() {
        return lr;
    }
    
    public Point getOul() {
        return oul;
    }
    
    public Point getOur() {
        return our;
    }
    
    public Point getOll() {
        return oll;
    }
    
    public Point getOlr() {
        return olr;
    }
    
    public class Point {
        private DoubleProperty x, y;
    
        public Point() {
            this(0, 0);
        }
    
        public Point(double x, double y) {
            this.x = new SimpleDoubleProperty(x);
            this.y = new SimpleDoubleProperty(y);
        }
    
        public void addListener(PointUpdate update) {
            x.addListener((observable, oldValue, newValue) -> {
                update.onUpdate(newValue.doubleValue(), y.get());
            });
            y.addListener((observable, oldValue, newValue) -> {
                update.onUpdate(x.get(), newValue.doubleValue());
            });
        }
        
        public void update() {
            x.setValue(x.get() + 1);
            x.setValue(x.get() - 1);
            y.setValue(y.get() + 1);
            y.setValue(y.get() - 1);
        }
    
        public double getX() {
            return x.get();
        }
    
        public DoubleProperty xProperty() {
            return x;
        }
    
        public void setX(double x) {
            this.x.set(x);
        }
    
        public double getY() {
            return y.get();
        }
    
        public DoubleProperty yProperty() {
            return y;
        }
    
        public void setY(double y) {
            this.y.set(y);
        }
    
        public void link(LocationInput input) {
            x.bindBidirectional(input.xProperty());
            y.bindBidirectional(input.yProperty());
        }
    }
    
    public interface PointUpdate {
        void onUpdate(double x, double y);
    
        static PointUpdate paneLayout(Pane pane) {
            return (x, y) -> {
                pane.setLayoutX(x - HALF_HANDLE);
                pane.setLayoutY(y - HALF_HANDLE);
            };
        }
    }
    
}
