package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.State;
import com.ssplugins.preedit.util.TemplateInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class EditTab extends BorderPane {
	
	private final Insets PADDING = new Insets(10);
	private Stage stage;
	private State state;

	private ToolBar toolbar;
	private ComboBox<String> selector;
	private Button btnNew;
	private Button btnSave;
	
	private EditorCanvas canvas;
	
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
		state = new State();
		state.setRenderCall(() -> {
			try {
				canvas.renderImage(true, layers.getItems());
			} catch (SilentFailException ignored) {
			}
		});
		defineNodes();
		Platform.runLater(() -> {
			selector.setItems(FXCollections.observableArrayList(PreEdit.getCatalog().getTemplates()));
		});
	}
	
	private void defineNodes() {
		toolbar = new ToolBar();
		this.setTop(toolbar);
		//
		selector = new ComboBox<>();
		selector.setPromptText("<select template>");
		selector.valueProperty().addListener((observable, oldValue, newValue) -> {
			// load new template
		});
		toolbar.getItems().add(selector);
		//
		btnNew = new Button("New");
		btnNew.setOnAction(event -> {
			Optional<TemplateInfo> op = Dialogs.newTemplate(null);
			op.ifPresent(info -> {
				Catalog catalog = PreEdit.getCatalog();
				if (catalog.templateExists(info.getName())) {
					Dialogs.show("Template already exists.", null, AlertType.INFORMATION);
					return;
				}
				resetNodes();
				Template template = catalog.newTemplate(info.getName(), info.getWidth(), info.getHeight());
				state.templateProperty().set(template);
				selector.getItems().add(template.getName());
				selector.setValue(template.getName());
			});
		});
		toolbar.getItems().add(btnNew);
		//
		btnSave = new Button("Save");
		btnSave.setDisable(true);
		btnSave.disableProperty().bind(state.savedProperty());
		toolbar.getItems().add(btnSave);
		//
		canvas = new EditorCanvas(400, 400);
		state.templateProperty().addListener((observable, oldValue, newValue) -> {
			canvas.clearAll();
			canvas.setCanvasSize(newValue.getWidth(), newValue.getHeight());
			layers.setItems(FXCollections.observableArrayList(newValue.getModules()));
			addLayer.setDisable(false);
			state.upToDateProperty().set(false);
		});
		BorderPane.setMargin(canvas, new Insets(10, 0, 10, 10));
		this.setCenter(canvas);
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
		addLayer.setOnAction(event -> {
			Optional<String> op = Dialogs.choose("Choose a module to add:", null, PreEdit.getCatalog().getModules());
			//
		});
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
	
	private void resetNodes() {
		state.savedProperty().set(true);
		canvas.clearAll();
		layers.getItems().clear();
		addLayer.setDisable(true);
		removeLayer.setDisable(true);
		layerUp.setDisable(true);
		layerDown.setDisable(true);
		effects.getItems().clear();
		addEffect.setDisable(true);
		removeEffect.setDisable(true);
		effectUp.setDisable(true);
		effectDown.setDisable(true);
		paramArea.getChildren().clear();
	}
	
}
