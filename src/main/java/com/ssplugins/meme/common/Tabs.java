package com.ssplugins.meme.common;

import com.ssplugins.meme.util.GUI;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public final class Tabs {
	
	public static final GridPane GENERATE = generate();
	public static final GridPane EDIT = edit();
	public static final GridPane SETTINGS = settings();
	
	private static GridPane generate() {
		GUI gui = new GUI(null);
		//
		return gui.getPane();
	}
	
	private static GridPane edit() {
		GUI gui = new GUI(null);
		gui.setPadding(10);
		gui.setGaps(10);
		
		Canvas canvas = new Canvas(350, 350);
		PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();
		for (int x = 0; x < 350; x++) {
			for (int y = 0; y < 350; y++) {
				pw.setColor(x, y, Color.WHITE);
			}
		}
		gui.add("canvas", 0, 0, canvas);
		
		GUI controls = new GUI(null);
		controls.setGaps(10);
		
		ComboBox<String> selector = new ComboBox<>();
		selector.setMinWidth(100);
		selector.setPromptText("<select meme>");
		controls.add("selector", 0, 0, selector);
		
		Button btnNew = new Button("New");
		controls.add("btnNew", 0, 1, btnNew);
		
		controls.add("line1", 1, 0, 1, 2, new Separator());
		
		Label labelLayers = new Label("Layers:");
		controls.add("labelLayers", 2, 0, labelLayers);
		
		// TODO layer stuff
		
		gui.add("controls", 0, 1, controls.getPane());
		return gui.getPane();
	}
	
	private static GridPane settings() {
		GUI gui = new GUI(null);
		//
		return gui.getPane();
	}
	
}
