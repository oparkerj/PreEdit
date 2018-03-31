package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.State;
import com.ssplugins.preedit.util.UITools;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class GenerateTab extends BorderPane {
	
	private final Insets PADDING = new Insets(10);
	private final int CANVAS_MIN = 400;
	
	private Stage stage;
	private State state;
	
	private ToolBar toolbar;
	private ComboBox<String> selector;
	private Button btnExport;
	private Button btnQuickSave;
	
	private ScrollPane canvasArea;
	private GridPane canvasPane;
	private EditorCanvas canvas;
	
	private GridPane controls;
	private Label labelLayers;
	private ListView<Module> layers;
	private Label labelEffects;
	private ListView<Effect> effects;
	
	private ScrollPane paramContainer;
	private FlowPane paramArea;
	
	public GenerateTab(Stage stage) {
		this.stage = stage;
		state = new State();
		state.setRenderCall(() -> {
			try {
				canvas.renderImage(true, state.getTemplate().getModules());
			} catch (SilentFailException ignored) {
			}
		});
		stage.sceneProperty().addListener((observable, oldValue, newValue) -> {
			newValue.focusOwnerProperty().addListener((observable1, oldNode, newNode) -> {
				if (newNode == layers) {
					getSelectedModule().ifPresent(module -> {
						module.linkResizeHandle(canvas.getHandle());
						setInputs(module.getInputs());
					});
				}
				else if (newNode == effects) {
					getSelectedEffect().ifPresent(effect -> {
						canvas.getHandle().hide();
						setInputs(effect.getInputs());
					});
				}
			});
		});
		defineNodes();
		Platform.runLater(this::updateTemplates);
	}
	
	public void updateTemplates() {
		selector.setItems(FXCollections.observableArrayList(PreEdit.getCatalog().getTemplates()));
	}
	
	private void defineNodes() {
		toolbar = new ToolBar();
		this.setTop(toolbar);
		//
		selector = new ComboBox<>();
		selector.setPromptText("<select template>");
		selector.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) return;
			Optional<Template> template = PreEdit.getCatalog().loadTemplate(newValue);
			if (template.isPresent()) {
				resetNodes();
				state.templateProperty().set(template.get());
			}
			else {
				Dialogs.show("Could not find template \"" + newValue + "\".", null, Alert.AlertType.WARNING);
			}
		});
		toolbar.getItems().add(selector);
		//
		btnExport = new Button("Export");
		btnExport.setDisable(true);
		btnExport.setOnAction(event -> {
			//
		});
		toolbar.getItems().add(btnExport);
		//
		btnQuickSave = new Button("Quick Save");
		btnQuickSave.setDisable(true);
		btnQuickSave.setOnAction(event -> {
		
		});
		toolbar.getItems().add(btnQuickSave);
		//
		canvasArea = new ScrollPane();
		canvasArea.setMinWidth(CANVAS_MIN);
		canvasArea.setMinHeight(CANVAS_MIN);
		canvasArea.setMinViewportWidth(CANVAS_MIN);
		canvasArea.setMinViewportHeight(CANVAS_MIN);
		canvasArea.setFitToWidth(true);
		canvasArea.setFitToHeight(true);
		BorderPane.setMargin(canvasArea, new Insets(10, 0, 10, 10));
		this.setCenter(canvasArea);
		//
		canvasPane = new GridPane();
		canvasPane.setAlignment(Pos.CENTER);
		canvasPane.prefWidthProperty().bind(canvasArea.widthProperty());
		canvasPane.prefHeightProperty().bind(canvasArea.heightProperty());
		canvasArea.setContent(canvasPane);
		//
		canvas = new EditorCanvas(CANVAS_MIN, CANVAS_MIN);
		state.templateProperty().addListener((observable, oldValue, newValue) -> {
			canvas.clearAll();
			canvas.setLayerCount(newValue.getModules().size());
			canvas.setCanvasSize(newValue.getWidth(), newValue.getHeight());
			layers.setItems(newValue.getModules());
			btnExport.setDisable(false);
			btnQuickSave.setDisable(false);
			state.render();
		});
		canvasPane.add(canvas, 0, 0);
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
		layers.setCellFactory(Module.getCellFactory());
		layers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				canvas.getHandle().hide();
				effects.setItems(null);
				setInputs(null);
				return;
			}
			newValue.linkResizeHandle(canvas.getHandle());
			effects.setItems(newValue.getEffects());
			setInputs(newValue.getInputs());
		});
		controls.add(layers, 0, 1);
		//
		labelEffects = new Label("Effects:");
		controls.add(labelEffects, 1, 0);
		//
		effects = new ListView<>();
		effects.setPrefWidth(150);
		effects.setPrefHeight(200);
		effects.setCellFactory(Effect.getCellFactory());
		effects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				setInputs(null);
				return;
			}
			canvas.getHandle().hide();
			setInputs(newValue.getInputs());
		});
		controls.add(effects, 1, 1);
		//
		paramContainer = new ScrollPane();
		paramContainer.setBorder(UITools.border(Color.BLACK));
		paramContainer.setMinViewportHeight(150);
		paramContainer.setFitToWidth(true);
		paramContainer.setFitToHeight(true);
		GridPane.setVgrow(paramContainer, Priority.ALWAYS);
		controls.add(paramContainer, 0, 2, 2, 1);
		//
		paramArea = new FlowPane(10, 10);
		paramArea.setMaxWidth(paramContainer.getViewportBounds().getWidth());
		paramArea.prefWrapLengthProperty().bind(paramArea.maxWidthProperty());
		paramContainer.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
			paramArea.setMaxWidth(newValue.getWidth());
		});
		StackPane.setMargin(paramArea, PADDING);
		paramContainer.setContent(paramArea);
	}
	
	private void resetNodes() {
		canvas.clearAll();
		ObservableList<Module> layerItems = layers.getItems();
		if (layerItems != null) layerItems.clear();
		ObservableList<Effect> effectItems = effects.getItems();
		if (effectItems != null) effectItems.clear();
		paramArea.getChildren().clear();
	}
	
	private void setInputs(InputMap map) {
		paramArea.getChildren().clear();
		if (map == null) return;
		UITools.setInputNodes(map, state, paramArea, true);
	}
	
	private Optional<Module> getSelectedModule() {
		return Optional.ofNullable(layers.getSelectionModel().getSelectedItem());
	}
	
	private Optional<Effect> getSelectedEffect() {
		return Optional.ofNullable(effects.getSelectionModel().getSelectedItem());
	}
	
}
