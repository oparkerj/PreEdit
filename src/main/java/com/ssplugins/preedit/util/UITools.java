package com.ssplugins.preedit.util;

import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.nodes.UserInput;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public final class UITools {
	
	public static void setInputNodes(InputMap map, State state, FlowPane paramArea, boolean gen) {
		map.getInputs().forEach((s, input) -> {
			input.setUpdateTrigger(state::render);
			if (gen) {
				if (!input.isUserProvided()) return;
				input.setGeneratorMode();
			}
			UserInput displayNode = input.getDisplayNode();
			displayNode.update(s);
			paramArea.getChildren().add(displayNode);
		});
	}
	
	public static Border border(Color color) {
		return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	}
	
}
