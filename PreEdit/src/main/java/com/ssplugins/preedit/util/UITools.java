package com.ssplugins.preedit.util;

import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.nodes.UserInput;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public final class UITools {
    
    public static void setInputNodes(InputMap map, State state, FlowPane paramArea, boolean gen) {
        map.sorted().forEach((s, input) -> {
            input.setUpdateTrigger(state::render);
            if (gen) {
                input.setGeneratorMode();
                if (!input.isUserProvided()) return;
            }
            UserInput displayNode = input.getDisplayNode();
            if (displayNode == null) return;
            displayNode.update(s);
            paramArea.getChildren().add(displayNode);
        });
    }
    
    public static Border border(Color color) {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    }
    
}
