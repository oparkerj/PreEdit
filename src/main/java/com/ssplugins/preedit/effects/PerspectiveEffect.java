package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.gui.EditorTab;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.nodes.EditorCanvas.NodeHandle;
import com.ssplugins.preedit.nodes.FreesizeHandle;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.PerspectiveTransform;

public class PerspectiveEffect extends Effect {
    
    private PerspectiveTransform transform;
    private FreesizeHandle handle;
    private NodeHandle nodeHandle;
    
    @Override
    protected void preload() {
        transform = new PerspectiveTransform();
        handle = new FreesizeHandle();
        transform.ulxProperty().bind(handle.getOul().xProperty());
        transform.ulyProperty().bind(handle.getOul().yProperty());
        transform.urxProperty().bind(handle.getOur().xProperty());
        transform.uryProperty().bind(handle.getOur().yProperty());
        transform.llxProperty().bind(handle.getOll().xProperty());
        transform.llyProperty().bind(handle.getOll().yProperty());
        transform.lrxProperty().bind(handle.getOlr().xProperty());
        transform.lryProperty().bind(handle.getOlr().yProperty());
        moduleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handle.boundsProperty().bind(newValue.getBounds());
            }
        });
    }
    
    @Override
    public void onSelectionChange(EditorTab tab, boolean selected) {
        if (nodeHandle == null) {
            nodeHandle = tab.getCanvas().createNodeHandle();
            nodeHandle.setNode(handle);
        }
        nodeHandle.toggle(selected);
    }
    
    @Override
    public void apply(Canvas canvas, GraphicsContext context, Node node, boolean editor) {
        quickApply(transform, canvas, node);
    }
    
    @Override
    public void reset() {
        transform.setInput(null);
    }
    
    @Override
    public String getName() {
        return "PerspectiveTransform";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        LocationInput ul = new LocationInput(false, false);
        handle.getUl().link(ul);
        map.addInput("Top Left", ul);
        LocationInput ur = new LocationInput(false, false);
        handle.getUr().link(ur);
        map.addInput("Top Right", ur);
        LocationInput ll = new LocationInput(false, false);
        handle.getLl().link(ll);
        map.addInput("Bottom Left", ll);
        LocationInput lr = new LocationInput(false, false);
        handle.getLr().link(lr);
        map.addInput("Bottom Right", lr);
    }
}
