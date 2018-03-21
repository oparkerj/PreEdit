package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.nodes.FillCanvas;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EditTab extends BorderPane {
	
	private final Insets PADDING = new Insets(10);
	private Stage stage;

	private ToolBar toolbar;
	private ComboBox<String> selector;
	private Button btnNew;
	private Button btnSave;
	
	private FillCanvas canvas;
	
	private GridPane controls;
	private Label labelLayers;
	private ListView<Module> layers;
	private VBox layerButtons;
	private Button addLayer;
	private Button removeLayer;
	private Button layerUp;
	private Button layerDown;
	private Label labelEffects;
	private ListView<Effect> effects;
	private VBox effectButtons;
	private Button addEffect;
	private Button removeEffect;
	private Button effectUp;
	private Button effectDown;
	
	private ScrollPane paramContainer;
	private FlowPane paramArea;
	
	public EditTab(Stage stage) {
		this.stage = stage;
		this.setMaxHeight(Double.MAX_VALUE);
		defineNodes();
	}
	
	private void defineNodes() {
		toolbar = new ToolBar();
		this.setTop(toolbar);
		//
		selector = new ComboBox<>();
		selector.setPromptText("<select template>");
		// TODO Template cell factory
		toolbar.getItems().add(selector);
		//
		btnNew = new Button("New");
		toolbar.getItems().add(btnNew);
		//
		btnSave = new Button("Save");
		btnSave.setDisable(true);
		toolbar.getItems().add(btnSave);
		//
		canvas = new FillCanvas(stage, 400, 400);
		Runnable onResize = () -> {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			for (int x = 0; x < canvas.getWidth(); x += 5) {
				for (int y = 0; y < canvas.getHeight(); y += 5) {
					Color c = ((x + y) % 2 == 0 ? Color.WHITE : Color.LIGHTGRAY);
					gc.setFill(c);
					gc.fillRect(x, y, 5, 5);
				}
			}
			gc.beginPath();
			gc.setStroke(Color.MAGENTA);
			gc.rect(canvas.getWidth() / 4, canvas.getHeight() / 4, canvas.getWidth() / 2, canvas.getHeight() / 2);
			gc.stroke();
		};
		canvas.setOnResize(onResize);
		onResize.run();
		BorderPane.setMargin(canvas, new Insets(10, 0, 10, 10));
		this.setCenter(canvas.getScrollPane());
		//
		controls = new GridPane();
		controls.setPadding(PADDING);
		controls.setHgap(5);
		controls.setVgap(3);
		this.setRight(controls);
		//
		labelLayers = new Label("Layers:");
		controls.add(labelLayers, 0, 0);
		//
		layers = new ListView<>();
		layers.setPrefWidth(150);
		layers.setPrefHeight(200);
		controls.add(layers, 0, 1);
		//
		layerButtons = new VBox(3);
		controls.add(layerButtons, 1, 1);
		//
		addLayer = smallButton("+");
		addLayer.setDisable(true);
		layerButtons.getChildren().add(addLayer);
		//
		removeLayer = smallButton("-");
		removeLayer.setDisable(true);
		layerButtons.getChildren().add(removeLayer);
		//
		layerUp = smallButton("^");
		layerUp.setDisable(true);
		layerButtons.getChildren().add(layerUp);
		//
		layerDown = smallButton("v");
		layerDown.setDisable(true);
		layerButtons.getChildren().add(layerDown);
		//
		labelEffects = new Label("Effects:");
		controls.add(labelEffects, 2, 0);
		//
		effects = new ListView<>();
		effects.setPrefWidth(150);
		effects.setPrefHeight(200);
		controls.add(effects, 2, 1);
		//
		effectButtons = new VBox(3);
		controls.add(effectButtons, 3, 1);
		//
		addEffect = smallButton("+");
		addEffect.setDisable(true);
		effectButtons.getChildren().add(addEffect);
		//
		removeEffect = smallButton("-");
		removeEffect.setDisable(true);
		effectButtons.getChildren().add(removeEffect);
		//
		effectUp = smallButton("^");
		effectUp.setDisable(true);
		effectButtons.getChildren().add(effectUp);
		//
		effectDown = smallButton("v");
		effectDown.setDisable(true);
		effectButtons.getChildren().add(effectDown);
		//
		paramContainer = new ScrollPane();
		paramContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		paramContainer.setPrefViewportHeight(150);
		paramContainer.setFitToHeight(true);
		paramContainer.setMaxWidth(350);
		controls.add(paramContainer, 0, 2, 4, 1);
//		//
		paramArea = new FlowPane(10, 10);
		paramArea.prefWrapLengthProperty().bind(paramContainer.widthProperty());
		paramContainer.setContent(paramArea);
		//
	}
	
	private Button smallButton(String text) {
		Button button = new Button(text);
		button.setMinWidth(25);
		button.setMinHeight(25);
		return button;
	}
}
