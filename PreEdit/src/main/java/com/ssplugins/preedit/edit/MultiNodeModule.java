package com.ssplugins.preedit.edit;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class MultiNodeModule extends NodeModule {
    
    private Pane pane;
    
    public abstract void addNodes();
    
    public final void addModule(NodeModule module) {
        if (module == this) return;
        pane.getChildren().add(module.getNode());
    }
    
    @Override
    public final Node getNode() {
        if (pane == null) {
            pane = new Pane();
            addNodes();
        }
        return pane;
    }
    
}
