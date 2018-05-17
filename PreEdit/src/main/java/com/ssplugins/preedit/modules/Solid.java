package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.NodeModule;
import com.ssplugins.preedit.input.ColorInput;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.nodes.ResizeHandle;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Solid extends NodeModule {
    
    private Rectangle rect;
    
    @Override
    protected void preload() {
        rect = new Rectangle(0, 0, 100, 100);
    }
    
    @Override
    public Node getNode() {
        return rect;
    }
    
    @Override
    public void linkResizeHandle(ResizeHandle handle) {
        getInputs().getInput("Location", LocationInput.class).ifPresent(handle::link);
    }
    
    @Override
    public ObservableValue<Bounds> getBounds() {
        return rect.layoutBoundsProperty();
    }
    
    @Override
    public String getName() {
        return "Solid";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        ColorInput color = new ColorInput();
        color.setValue(Color.WHITE);
        rect.fillProperty().bind(color.valueProperty());
        map.addInput("Color", color);
        LocationInput location = new LocationInput(true, true, 0, 0, 100, 100, 0);
        rect.xProperty().bind(location.xProperty());
        rect.yProperty().bind(location.yProperty());
        //        rect.layoutXProperty().bind(location.xProperty());
        //        rect.layoutYProperty().bind(location.yProperty());
        rect.widthProperty().bind(location.widthProperty());
        rect.heightProperty().bind(location.heightProperty());
        rect.rotateProperty().bind(location.angleProperty());
        map.addInput("Location", location);
    }
    
}
