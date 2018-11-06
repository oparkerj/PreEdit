package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.nodes.NumberField;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.UndoHistory;
import com.ssplugins.preedit.util.wrapper.GridMap;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;

import java.text.NumberFormat;

public class LocationInput extends Input<GridMap, Bounds> {
    
    private IntegerProperty x, y, width, height;
    private DoubleProperty angle;
    private boolean size, rotate;
    
    private ObjectProperty<Bounds> bounds;
    
    public LocationInput(boolean size, boolean rotate) {
        this(size, rotate, 0, 0, 100, 100, 0);
    }
    
    public LocationInput(boolean size, boolean rotate, int x, int y, int width, int height, double angle) {
        this.x = new SimpleIntegerProperty(x);
        this.y = new SimpleIntegerProperty(y);
        this.width = new SimpleIntegerProperty(width);
        this.height = new SimpleIntegerProperty(height);
        this.angle = new SimpleDoubleProperty(angle);
        this.size = size;
        this.rotate = rotate;
        this.bounds = new SimpleObjectProperty<>(new BoundingBox(x, y, angle, width, height, 0));
        boundsListener();
        this.ready();
    }
    
    private void boundsListener() {
        x.addListener((observable, oldValue, newValue) -> {
            bounds.set(new BoundingBox(newValue.doubleValue(), getY(), getAngle(), getWidth(), getHeight(), 0));
        });
        y.addListener((observable, oldValue, newValue) -> {
            bounds.set(new BoundingBox(getX(), newValue.doubleValue(), getAngle(), getWidth(), getHeight(), 0));
        });
        width.addListener((observable, oldValue, newValue) -> {
            bounds.set(new BoundingBox(getX(), getY(), getAngle(), newValue.doubleValue(), getHeight(), 0));
        });
        height.addListener((observable, oldValue, newValue) -> {
            bounds.set(new BoundingBox(getX(), getY(), getAngle(), getWidth(), newValue.doubleValue(), 0));
        });
        angle.addListener((observable, oldValue, newValue) -> {
            bounds.set(new BoundingBox(getX(), getY(), newValue.doubleValue(), getWidth(), getHeight(), 0));
        });
        bounds.addListener((observable, oldValue, newValue) -> {
            setNodeValue(getNode(), newValue);
        });
    }
    
    public boolean isSizeable() {
        return size;
    }
    
    public boolean canSpin() {
        return rotate;
    }
    
    public int getX() {
        return x.get();
    }
    
    public IntegerProperty xProperty() {
        return x;
    }
    
    public int getY() {
        return y.get();
    }
    
    public IntegerProperty yProperty() {
        return y;
    }
    
    public int getWidth() {
        return width.get();
    }
    
    public IntegerProperty widthProperty() {
        return width;
    }
    
    public int getHeight() {
        return height.get();
    }
    
    public IntegerProperty heightProperty() {
        return height;
    }
    
    public double getAngle() {
        return angle.get();
    }
    
    public DoubleProperty angleProperty() {
        return angle;
    }
    
    public ObservableValue<Bounds> boundsProperty() {
        return bounds;
    }
    
    @Override
    protected void setNodeValue(GridMap node, Bounds value) {
        x.set((int) value.getMinX());
        y.set((int) value.getMinY());
        width.set((int) value.getWidth());
        height.set((int) value.getHeight());
        angle.set(value.getMinZ());
    }
    
    @Override
    protected void setUpdateTrigger(GridMap node, Runnable update) {
        x.addListener(observable -> update.run());
        y.addListener(observable -> update.run());
        width.addListener(observable -> update.run());
        height.addListener(observable -> update.run());
        angle.addListener(observable -> update.run());
    }
    
    @Override
    protected JsonConverter<Bounds> getJsonConverter() {
        return new JsonConverter<Bounds>() {
            @Override
            public JsonElement toJson(Bounds bounds) {
                JsonObject out = new JsonObject();
                out.addProperty("x", bounds.getMinX());
                out.addProperty("y", bounds.getMinY());
                out.addProperty("width", bounds.getWidth());
                out.addProperty("height", bounds.getHeight());
                out.addProperty("angle", bounds.getMinZ());
                return out;
            }
            
            @Override
            public Bounds fromJson(JsonElement element) {
                if (element.isJsonNull()) return new BoundingBox(0, 0, 100, 100);
                JsonObject json = element.getAsJsonObject();
                return new BoundingBox(json.get("x").getAsInt(), json.get("y").getAsInt(), json.get("angle").getAsDouble(), json.get("width").getAsInt(), json.get("height").getAsInt(), 0);
            }
        };
    }
    
    @Override
    protected GridMap createInputNode() {
        GridMap map = new GridMap();
        map.setHgap(5);
        map.setVgap(5);
        map.add(null, 0, 0, new Label("x:"));
        NumberField fieldX = new NumberField(x.get());
        fieldX.setPrefWidth(50);
        fieldX.numberProperty().bindBidirectional(x);
        map.add("x", 0, 1, fieldX);
        map.add(null, 0, 2, new Label("y:"));
        NumberField fieldY = new NumberField(y.get());
        fieldY.setPrefWidth(50);
        fieldY.numberProperty().bindBidirectional(y);
        map.add("y", 0, 3, fieldY);
        //
        if (size) {
            map.add(null, 1, 0, new Label("width:"));
            NumberField fieldW = new NumberField(width.get());
            fieldW.setPrefWidth(50);
            fieldW.numberProperty().bindBidirectional(width);
            map.add("width", 1, 1, fieldW);
            map.add(null, 1, 2, new Label("height:"));
            NumberField fieldH = new NumberField(height.get());
            fieldH.setPrefWidth(50);
            fieldH.numberProperty().bindBidirectional(height);
            map.add("height", 1, 3, fieldH);
        }
        if (rotate) {
            map.add(null, 2, 0, new Label("angle:"));
            NumberField fieldR = new NumberField(angle.get(), NumberFormat.getNumberInstance());
            fieldR.setPrefWidth(50);
            fieldR.numberProperty().bindBidirectional(angle);
            map.add("angle", 2, 1, fieldR);
        }
        return map;
    }
    
    @Override
    protected Bounds getNodeValue(GridMap node) throws InvalidInputException {
        try {
            return new BoundingBox(getX(), getY(), getAngle(), getWidth(), getHeight(), 0);
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }
    
    @Override
    protected boolean isValid(Bounds value) {
        if (size) {
            return value.getWidth() > 0 && value.getHeight() > 0;
        }
        return true;
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        UndoHistory.UndoTrigger trigger = undoHistory.createTrigger();
        bounds.addListener((observable, oldValue, newValue) -> {
            if (oldValue.getMinX() == newValue.getMinX() && oldValue.getMinY() == newValue.getMinY()) {
                if (oldValue.getWidth() != newValue.getWidth() || oldValue.getHeight() != newValue.getHeight()) {
                    if (size) trigger.submit(bounds, oldValue);
                }
                else if (oldValue.getMinZ() != newValue.getMinZ()) {
                    if (rotate) trigger.submit(bounds, oldValue);
                }
            }
            else {
                trigger.submit(bounds, oldValue);
            }
        });
    }
}
