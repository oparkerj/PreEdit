package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.State;
import com.ssplugins.preedit.util.TemplateInfo;
import com.ssplugins.preedit.util.UITools;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditTab extends BorderPane {
	
	//
	private final Insets PADDING = new Insets(10);
	private final int CANVAS_MIN = 400;
	
	private Stage stage;
	private State state;
	private AtomicBoolean loading = new AtomicBoolean(false);

	private ToolBar toolbar;
	private ComboBox<String> selector;
	private Button btnNew;
	private Button btnSave;
	
	private ScrollPane canvasArea;
	private GridPane canvasPane;
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
				canvas.renderImage(true, layers.getItems(), true);
			} catch (SilentFailException ignored) {
//				Dialogs.exception("debug", null, ignored);
			}
		});
		stage.sceneProperty().addListener((observable, oldValue, newValue) -> {
			newValue.focusOwnerProperty().addListener((observable1, oldNode, newNode) -> {
				if (newNode == layers) {
					getSelectedModule().ifPresent(module -> {
						setInputs(module.getInputs());
						module.linkResizeHandle(canvas.getHandleUnbound());
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
			if (newValue == null) return;
			if (loading.get()) {
				loading.set(false);
				return;
			}
			checkSave();
			Optional<Template> template = PreEdit.getCatalog().loadTemplate(newValue);
			if (template.isPresent()) {
				if (!state.isSaved()) {
					selector.getItems().remove(oldValue);
					return;
				}
				resetNodes();
				state.templateProperty().set(template.get());
			}
			else {
				Dialogs.show("Could not find template \"" + newValue + "\".", null, AlertType.WARNING);
			}
		});
		toolbar.getItems().add(selector);
		//
		btnNew = new Button("New");
		btnNew.setOnAction(event -> {
			Optional<TemplateInfo> op = Dialogs.newTemplate(null);
			op.ifPresent(info -> {
				checkSave();
				Catalog catalog = PreEdit.getCatalog();
				if (catalog.templateExists(info.getName())) {
					Dialogs.show("Template already exists.", null, AlertType.INFORMATION);
					return;
				}
				resetNodes();
				Template template = catalog.newTemplate(info.getName(), info.getWidth(), info.getHeight());
				loading.set(true);
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
		btnSave.setOnAction(event -> {
			PreEdit.getCatalog().saveTemplate(state.getTemplate());
			state.savedProperty().set(true);
		});
		toolbar.getItems().add(btnSave);
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
			addLayer.setDisable(false);
			if (loading.get()) state.render();
			else state.renderPassive();
		});
		canvas.addEventFilter(MouseEvent.ANY, event -> {
			getSelectedModule().ifPresent(module -> module.onMouseEvent(event, true));
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
				removeLayer.setDisable(true);
				layerUp.setDisable(true);
				layerDown.setDisable(true);
				effects.setItems(null);
				addEffect.setDisable(true);
				setInputs(null);
				return;
			}
			removeLayer.setDisable(false);
			layerUp.setDisable(false);
			layerDown.setDisable(false);
			setInputs(newValue.getInputs());
			newValue.linkResizeHandle(canvas.getHandleUnbound());
			effects.setItems(newValue.getEffects());
			addEffect.setDisable(false);
		});
		controls.add(layers, 0, 1);
		//
		layerButtons = new VBox(3);
		controls.add(layerButtons, 1, 1);
		//
		addLayer = smallButton("+");
		addLayer.setDisable(true);
		addLayer.setOnAction(event -> {
			Optional<String> op = Dialogs.choose("Choose a module to add:", null, PreEdit.getCatalog().getModules());
			op.flatMap(PreEdit.getCatalog()::createModule).ifPresent(module -> {
				layers.getItems().add(0, module);
				canvas.addLayer();
				state.render();
			});
		});
		layerButtons.getChildren().add(addLayer);
		//
		removeLayer = smallButton("-");
		removeLayer.setDisable(true);
		removeLayer.setOnAction(event -> {
			getSelectedModule().ifPresent(module -> {
				layers.getItems().remove(module);
				canvas.removeLayer();
				state.render();
			});
		});
		layerButtons.getChildren().add(removeLayer);
		//
		layerUp = smallButton("^");
		layerUp.setDisable(true);
		layerUp.setOnAction(event -> {
			shiftUp(layers);
			state.render();
		});
		layerButtons.getChildren().add(layerUp);
		//
		layerDown = smallButton("v");
		layerDown.setDisable(true);
		layerDown.setOnAction(event -> {
			shiftDown(layers);
			state.render();
		});
		layerButtons.getChildren().add(layerDown);
		//
		labelEffects = new Label("Effects:");
		controls.add(labelEffects, 2, 0);
		//
		effects = new ListView<>();
		effects.setPrefWidth(150);
		effects.setPrefHeight(200);
		effects.setCellFactory(Effect.getCellFactory());
		effects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				removeEffect.setDisable(true);
				effectUp.setDisable(true);
				effectDown.setDisable(true);
				setInputs(null);
				return;
			}
			removeEffect.setDisable(false);
			effectUp.setDisable(false);
			effectDown.setDisable(false);
			canvas.getHandle().hide();
			setInputs(newValue.getInputs());
		});
		controls.add(effects, 2, 1);
		//
		effectButtons = new VBox(3);
		controls.add(effectButtons, 3, 1);
		//
		addEffect = smallButton("+");
		addEffect.setDisable(true);
		addEffect.setOnAction(event -> {
			Optional<String> op = Dialogs.choose("Choose an effect to add:", null, PreEdit.getCatalog().getEffects());
			op.flatMap(PreEdit.getCatalog()::createEffect).ifPresent(effect -> {
				effects.getItems().add(0, effect);
				state.render();
			});
		});
		effectButtons.getChildren().add(addEffect);
		//
		removeEffect = smallButton("-");
		removeEffect.setDisable(true);
		removeEffect.setOnAction(event -> {
			getSelectedEffect().ifPresent(effect -> {
				effects.getItems().remove(effect);
				state.render();
			});
		});
		effectButtons.getChildren().add(removeEffect);
		//
		effectUp = smallButton("^");
		effectUp.setDisable(true);
		effectUp.setOnAction(event -> {
			shiftUp(effects);
			state.render();
		});
		effectButtons.getChildren().add(effectUp);
		//
		effectDown = smallButton("v");
		effectDown.setDisable(true);
		effectDown.setOnAction(event -> {
			shiftDown(effects);
			state.render();
		});
		effectButtons.getChildren().add(effectDown);
		//
		paramContainer = new ScrollPane();
		paramContainer.setBorder(UITools.border(Color.BLACK));
		paramContainer.setMinViewportHeight(150);
		paramContainer.setFitToWidth(true);
		paramContainer.setFitToHeight(true);
		GridPane.setVgrow(paramContainer, Priority.ALWAYS);
		controls.add(paramContainer, 0, 2, 4, 1);
		//
		paramArea = new FlowPane(10, 10);
		paramArea.setMaxWidth(paramContainer.getViewportBounds().getWidth());
		paramArea.prefWrapLengthProperty().bind(paramArea.maxWidthProperty());
		paramContainer.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
			paramArea.setMaxWidth(newValue.getWidth());
		});
		StackPane.setMargin(paramArea, PADDING);
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
		ObservableList<Module> layerItems = layers.getItems();
		if (layerItems != null) layerItems.clear();
		addLayer.setDisable(true);
		removeLayer.setDisable(true);
		layerUp.setDisable(true);
		layerDown.setDisable(true);
		ObservableList<Effect> effectItems = effects.getItems();
		if (effectItems != null) effectItems.clear();
		addEffect.setDisable(true);
		removeEffect.setDisable(true);
		effectUp.setDisable(true);
		effectDown.setDisable(true);
		paramArea.getChildren().clear();
	}
	
	private void setInputs(InputMap map) {
		paramArea.getChildren().clear();
		if (map == null) return;
		UITools.setInputNodes(map, state, paramArea, false);
	}
	
	private Optional<Module> getSelectedModule() {
		return Optional.ofNullable(layers.getSelectionModel().getSelectedItem());
	}
	
	private Optional<Effect> getSelectedEffect() {
		return Optional.ofNullable(effects.getSelectionModel().getSelectedItem());
	}
	
	private void checkSave() {
		if (!state.isSaved()) {
			Optional<ButtonType> op = Dialogs.saveDialog("Save the current template before loading?", null);
			op.filter(buttonType -> buttonType.getButtonData() == ButtonBar.ButtonData.YES)
			  .ifPresent(buttonType -> PreEdit.getCatalog().saveTemplate(state.getTemplate()));
		}
	}
	
	private <T> void shiftUp(ListView<T> list) {
		MultipleSelectionModel<T> selection = list.getSelectionModel();
		if (selection.isEmpty()) return;
		int r = selection.getSelectedIndex();
		if (r == 0) return;
		T removed = list.getItems().remove(r);
		r--;
		list.getItems().add(r, removed);
		selection.select(r);
		list.scrollTo(r);
	}
	
	private <T> void shiftDown(ListView<T> list) {
		MultipleSelectionModel<T> selection = list.getSelectionModel();
		if (selection.isEmpty()) return;
		int r = selection.getSelectedIndex();
		if (r >= list.getItems().size() - 1) return;
		T removed = list.getItems().remove(r);
		r++;
		list.getItems().add(r, removed);
		selection.select(r);
		list.scrollTo(r);
	}
	
}
