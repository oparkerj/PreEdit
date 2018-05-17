package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.NodeModule;
import com.ssplugins.preedit.input.HiddenInput;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.nodes.ResizeHandle;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageModule extends NodeModule {
    
    private Image image;
    private ImageView view;
    
    public void setImage(Image image, boolean init) {
        this.image = image;
        view.setImage(image);
        if (image != null && !init) {
            getInputs().getInput("Location", LocationInput.class).ifPresent(input -> {
                input.widthProperty().set((int) image.getWidth());
                input.heightProperty().set((int) image.getHeight());
            });
        }
    }
    
    @Override
    protected void preload() {
        view = new ImageView();
    }
    
    @Override
    public Node getNode() {
        return view;
    }
    
    @Override
    public void linkResizeHandle(ResizeHandle handle) {
        getInputs().getInput("Location", LocationInput.class).ifPresent(handle::link);
    }
    
    @Override
    public ObservableValue<Bounds> getBounds() {
        return view.layoutBoundsProperty();
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        LocationInput location = new LocationInput(true, true);
        view.xProperty().bind(location.xProperty());
        view.yProperty().bind(location.yProperty());
        location.widthProperty().addListener((observable, oldValue, newValue) -> {
            view.prefWidth(newValue.doubleValue());
            view.setFitWidth(newValue.doubleValue());
        });
        location.heightProperty().addListener((observable, oldValue, newValue) -> {
            view.prefHeight(newValue.doubleValue());
            view.setFitHeight(newValue.doubleValue());
        });
        view.rotateProperty().bind(location.angleProperty());
        map.addInput("Location", location);
        map.addInput("hidden", new HiddenInput());
    }
    
}
