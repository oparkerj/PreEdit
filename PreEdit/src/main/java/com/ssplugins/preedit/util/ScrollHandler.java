package com.ssplugins.preedit.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

public class ScrollHandler {
    
    private DoubleProperty output;
    private Range range;
    private double smallDelta, delta;
    private int invert = 1;
    
    public ScrollHandler(Node node, Range range, double smallDelta, double delta) {
        output = new SimpleDoubleProperty(1);
        this.range = range;
        this.smallDelta = smallDelta;
        this.delta = delta;
        node.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!event.isAltDown()) {
                return;
            }
            double d = event.isControlDown() ? smallDelta : delta;
            d *= invert * Math.signum(event.getDeltaY());
            output.set(range.clamp(output.get() + d));
            event.consume();
        });
    }
    
    public void setInvert(boolean invert) {
        this.invert = invert ? -1 : 1;
    }
    
    public DoubleProperty outputProperty() {
        return output;
    }
    
}
