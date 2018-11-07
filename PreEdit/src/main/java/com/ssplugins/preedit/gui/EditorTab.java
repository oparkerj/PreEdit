package com.ssplugins.preedit.gui;

import com.google.gson.JsonParseException;
import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.api.PreEditTab;
import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.exceptions.SimpleException;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.LocationInput;
import com.ssplugins.preedit.modules.FileImage;
import com.ssplugins.preedit.modules.TextModule;
import com.ssplugins.preedit.modules.URLImage;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.util.*;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EditorTab extends BorderPane implements PreEditTab {
    
    //
    private final Insets PADDING = new Insets(10);
    private final int CANVAS_MIN = 400;
    
    private PreEdit base;
    private Stage stage;
    private State state;
    private AtomicBoolean loading = new AtomicBoolean(false);
    private boolean editControls;
    
    private UndoHistory undoHistory;
    
    private ToolBar toolbar;
    private ComboBox<String> selector;
    private Button btnNew;
    private Button btnSave;
    private Button btnDelete;
    //
    private Button btnExport;
    private Button btnQuickSave;
    private Button btnCopy;
    
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
    
    public EditorTab(boolean editControls, PreEdit base) {
        this.base = base;
        this.stage = base.getStage();
        this.editControls = editControls;
        state = new State();
        state.setRenderCall(() -> {
            try {
                canvas.renderImage(true, layers.getItems(), editControls);
            } catch (SilentFailException ignored) {
                //				Dialogs.exception("debug", null, ignored);
            }
        });
        stage.sceneProperty().addListener((observable, oldValue, newValue) -> {
            newValue.focusOwnerProperty().addListener((observable1, oldNode, newNode) -> {
                if (newNode == layers) {
                    getSelectedModule().ifPresent(module -> {
                        setInputs(module.getInputs());
                        module.onSelectionChange(this, true);
                        module.linkResizeHandle(canvas.getHandleUnbound());
                    });
                    getSelectedEffect().ifPresent(effect -> {
                        effect.onSelectionChange(this, false);
                    });
                }
                else if (newNode == effects) {
                    getSelectedEffect().ifPresent(effect -> {
                        canvas.getHandle().hide();
                        effect.onSelectionChange(this, true);
                        setInputs(effect.getInputs());
                    });
                    getSelectedModule().ifPresent(module -> {
                        module.onSelectionChange(this, false);
                    });
                }
            });
        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (editControls) {
                if (event.getCode() == KeyCode.S && event.isControlDown()) {
                    save();
                }
            }
            if (event.getCode() == KeyCode.Z && event.isControlDown()) {
                undoHistory.undo();
            }
            else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
                undoHistory.redo();
            }
        });
        undoHistory = new UndoHistory(stage);
        undoHistory.setOnUpdate(state::render);
        defineNodes();
        Platform.runLater(this::updateTemplates);
    }
    
    void updateTemplates() {
        selector.setItems(FXCollections.observableArrayList(base.getCatalog().getTemplates()));
    }
    
    public State getState() {
        return state;
    }
    
    public EditorCanvas getCanvas() {
        return canvas;
    }
    
    public UndoHistory getUndoHistory() {
        return undoHistory;
    }
    
    public void checkSave() {
        if (!state.isSaved()) {
            Optional<ButtonType> op = Dialogs.saveDialog("Save the current template?", null);
            op.filter(buttonType -> buttonType.getButtonData() == ButtonBar.ButtonData.YES).ifPresent(buttonType -> save());
        }
    }
    
    public void save() {
        base.getCatalog().saveTemplate(state.getTemplate());
        state.savedProperty().set(true);
    }
    
    @Override
    public ToolBar getToolbar() {
        return toolbar;
    }
    
    @Override
    public void addToolbarNode(Node node) {
        toolbar.getItems().add(node);
    }
    
    @Override
    public BooleanBinding templateLoadedProperty() {
        return state.templateLoadedProperty();
    }
    
    @Override
    public Optional<BufferedImage> renderImage() {
        return renderPNG();
    }
    
    private void defineNodes() {
        toolbar = new ToolBar();
        this.setTop(toolbar);
        //
        selector = new ComboBox<>();
        selector.setPromptText("<select template>");
        selector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                resetNodes();
                return;
            }
            if (loading.get()) {
                loading.set(false);
                return;
            }
            checkSave();
            try {
                Optional<Template> template = base.getCatalog().loadTemplate(newValue);
                if (template.isPresent()) {
                    if (!state.isSaved() && !base.getCatalog().templateExists(oldValue)) {
                        selector.getItems().remove(oldValue);
                        return;
                    }
                    resetNodes();
                    state.templateProperty().set(template.get());
                }
                else {
                    Dialogs.show("Could not find template \"" + newValue + "\".", null, AlertType.WARNING);
                }
            } catch (JsonParseException e) {
                if (e.getCause() instanceof SimpleException) {
                    Dialogs.show(e.getCause().getMessage(), null, AlertType.WARNING);
                }
            }
        });
        toolbar.getItems().add(selector);
        //
        if (editControls) {
            btnNew = new Button("New");
            btnNew.setOnAction(event -> {
                Optional<TemplateInfo> op = Dialogs.newTemplate(null);
                op.ifPresent(info -> {
                    checkSave();
                    Catalog catalog = base.getCatalog();
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
                save();
            });
            toolbar.getItems().add(btnSave);
            //
            btnDelete = new Button("Delete");
            btnDelete.setDisable(true);
            btnDelete.setOnAction(event -> {
                Optional<ButtonType> op = Dialogs.confirm("Are you sure you want to delete the current template?", null);
                op.filter(button -> button.getButtonData() == ButtonBar.ButtonData.YES).ifPresent(button -> {
                    base.getCatalog().removeTemplate(state.getTemplate());
                    base.getCatalog().saveData();
                    resetNodes();
                    updateTemplates();
                });
            });
            toolbar.getItems().addAll(btnDelete);
        }
        else {
            btnExport = new Button("Export");
            btnExport.setDisable(true);
            btnExport.setOnAction(event -> {
                Dialogs.saveFile(stage, null).ifPresent(this::exportImage);
            });
            toolbar.getItems().add(btnExport);
            //
            btnQuickSave = new Button("Quick Save");
            btnQuickSave.setDisable(true);
            btnQuickSave.setOnAction(event -> {
                exportImage(new File("image.png"));
            });
            toolbar.getItems().add(btnQuickSave);
            //
            btnCopy = new Button("Copy to Clipboard");
            btnCopy.setOnAction(event -> {
                renderRaw().ifPresent(image -> {
                    Clipboard board = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(image);
                    board.setContent(content);
                    
                    Popup popup = new Popup();
                    popup.setAutoFix(true);
                    popup.setAutoHide(true);
                    popup.getContent().add(new Label("Copied"));
                    Bounds bounds = btnCopy.localToScreen(btnCopy.getBoundsInLocal());
                    popup.show(btnCopy, bounds.getMaxX(), bounds.getMinY());
                });
                state.render();
            });
            btnCopy.setDisable(true);
            btnCopy.disableProperty().bind(templateLoadedProperty().not());
            toolbar.getItems().add(btnCopy);
        }
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
            if (newValue == null) return;
            newValue.setEditor(editControls);
            canvas.clearAll();
            canvas.setLayerCount(newValue.getModules().size());
            canvas.setCanvasSize(newValue.getWidth(), newValue.getHeight());
            layers.setItems(newValue.getModules());
            newValue.getModules().forEach(module -> {
                module.getInputs().getInputs().forEach((s, input) -> {
                    input.setUpdateTrigger(state::renderPassive);
                });
                module.getEffects().forEach(effect -> {
                    effect.getInputs().getInputs().forEach((s, input) -> {
                        input.setUpdateTrigger(state::renderPassive);
                    });
                });
            });
            disable(btnDelete, false);
            disable(addLayer, false);
            disable(btnExport, false);
            disable(btnQuickSave, false);
            if (loading.get()) state.render();
            else state.renderPassive();
        });
        canvas.addEventFilter(MouseEvent.ANY, event -> {
            getSelectedModule().ifPresent(module -> module.onMouseEvent(event, editControls));
        });
        canvasPane.add(canvas, 0, 0);
        ///////////////////// Draggables ///////////////////////
        if (editControls) {
            canvasArea.setOnDragOver(event -> {
                if (state.getTemplate() == null) return;
                Dragboard board = event.getDragboard();
                if (board.hasFiles() || board.hasImage() || board.hasRtf() || board.hasString() || board.hasUrl()) {
                    event.acceptTransferModes(TransferMode.LINK);
                }
                else event.consume();
            });
            canvasArea.setOnDragDropped(event -> {
                if (state.getTemplate() == null) return;
                Dragboard board = event.getDragboard();
                boolean dropped = false;
                if (board.hasFiles()) {
                    dropped = true;
                    try {
                        File file = board.getFiles().get(0);
                        String name = file.getName().toLowerCase();
                        if (!name.endsWith(".png") && !name.endsWith(".jpg") && !name.endsWith(".jpeg") && !name.endsWith(".gif") && !name.endsWith(".bmp"))
                            throw new IllegalArgumentException();
                        Optional<FileImage> op = base.getCatalog().createModule(FileImage.class);
                        op.ifPresent(fileImage -> {
                            this.addModule(fileImage);
                            fileImage.setFile(file);
                            fileImage.getInputs().getInput("Location", LocationInput.class).ifPresent(locationInput -> {
                                Point2D p = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
                                locationInput.xProperty().setValue(p.getX());
                                locationInput.yProperty().setValue(p.getY());
                            });
                            layers.getSelectionModel().select(fileImage);
                        });
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                else if (board.hasUrl()) {
                    try {
                        String url = board.getUrl();
                        Optional<URLImage> op = base.getCatalog().createModule(URLImage.class);
                        op.ifPresent(urlImage -> {
                            this.addModule(urlImage);
                            urlImage.setURL(url);
                            urlImage.getInputs().getInput("Location", LocationInput.class).ifPresent(locationInput -> {
                                Point2D p = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
                                locationInput.xProperty().setValue(p.getX());
                                locationInput.yProperty().setValue(p.getY());
                            });
                            layers.getSelectionModel().select(urlImage);
                        });
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                else if (board.hasRtf()) {
                    String text = board.getRtf();
                    Optional<TextModule> op = base.getCatalog().createModule(TextModule.class);
                    dragTextToModule(event, text, op);
                }
                else if (board.hasString()) {
                    String text = board.getString();
                    Optional<TextModule> op = base.getCatalog().createModule(TextModule.class);
                    dragTextToModule(event, text, op);
                }
                event.setDropCompleted(dropped);
                event.consume();
            });
        }
        ////////////////////////////////////////////////////////
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
            if (oldValue != null) oldValue.onSelectionChange(this, false);
            if (newValue == null) {
                canvas.getHandle().hide();
                disable(removeLayer, true);
                disable(layerUp, true);
                disable(layerDown, true);
                effects.setItems(null);
                disable(addEffect, true);
                setInputs(null);
                return;
            }
            newValue.onSelectionChange(this, true);
            disable(removeLayer, false);
            disable(layerUp, false);
            disable(layerDown, false);
            setInputs(newValue.getInputs());
            newValue.linkResizeHandle(canvas.getHandleUnbound());
            effects.setItems(newValue.getEffects());
            disable(addEffect, false);
            state.render();
        });
        controls.add(layers, 0, 1);
        //
        if (editControls) {
            layerButtons = new VBox(3);
            controls.add(layerButtons, 1, 1);
            //
            addLayer = smallButton("+");
            addLayer.setDisable(true);
            addLayer.setOnAction(event -> {
                Optional<String> op = Dialogs.choose("Choose a module to add:", null, base.getCatalog().getModules());
                op.flatMap(base.getCatalog()::createModule).ifPresent(this::addModule);
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
        }
        //
        labelEffects = new Label("Effects:");
        controls.add(labelEffects, 2, 0);
        //
        effects = new ListView<>();
        effects.setPrefWidth(150);
        effects.setPrefHeight(200);
        effects.setCellFactory(Effect.getCellFactory());
        effects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) oldValue.onSelectionChange(this, false);
            if (newValue == null) {
                disable(removeEffect, true);
                disable(effectUp, true);
                disable(effectDown, true);
                setInputs(null);
                return;
            }
            newValue.onSelectionChange(this, true);
            disable(removeEffect, false);
            disable(effectUp, false);
            disable(effectDown, false);
            canvas.getHandle().hide();
            setInputs(newValue.getInputs());
            state.render();
        });
        controls.add(effects, 2, 1);
        //
        effectButtons = new VBox(3);
        controls.add(effectButtons, 3, 1);
        //
        if (editControls) {
            addEffect = smallButton("+");
            addEffect.setDisable(true);
            addEffect.setOnAction(event -> {
                Optional<String> op = Dialogs.choose("Choose an effect to add:", null, base.getCatalog().getEffects());
                op.flatMap(base.getCatalog()::createEffect).ifPresent(this::addEffect);
            });
            effectButtons.getChildren().add(addEffect);
            //
            removeEffect = smallButton("-");
            removeEffect.setDisable(true);
            removeEffect.setOnAction(event -> {
                getSelectedModule().ifPresent(module -> {
                    getSelectedEffect().ifPresent(effect -> {
                        module.removeEffect(effect);
                        state.render();
                    });
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
        }
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
    
    private void dragTextToModule(DragEvent event, String text, Optional<TextModule> op) {
        op.ifPresent(textModule -> {
            this.addModule(textModule);
            textModule.setText(text);
            textModule.getInputs().getInput("Location", LocationInput.class).ifPresent(locationInput -> {
                Point2D p = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
                locationInput.xProperty().setValue(p.getX());
                locationInput.yProperty().setValue(p.getY());
            });
            layers.getSelectionModel().select(textModule);
        });
    }
    
    private Button smallButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(25);
        button.setMinHeight(25);
        return button;
    }
    
    private void disable(Node node, boolean disable) {
        if (node == null) return;
        node.setDisable(disable);
    }
    
    private void resetNodes() {
        state.savedProperty().set(true);
        disable(btnDelete, true);
        disable(btnExport, true);
        disable(btnQuickSave, true);
        //		disable(btnCopy, true);
        canvas.clearAll();
        ObservableList<Module> layerItems = layers.getItems();
        if (layerItems != null) layerItems.clear();
        disable(addLayer, true);
        disable(removeLayer, true);
        disable(layerUp, true);
        disable(layerDown, true);
        ObservableList<Effect> effectItems = effects.getItems();
        if (effectItems != null) effectItems.clear();
        disable(addEffect, true);
        disable(removeEffect, true);
        disable(effectUp, true);
        disable(effectDown, true);
        paramArea.getChildren().clear();
        state.templateProperty().set(null);
    }
    
    private void setInputs(InputMap map) {
        paramArea.getChildren().clear();
        if (map == null) return;
        map.getInputs().forEach((s, input) -> input.linkUndoHistory(undoHistory));
        UITools.setInputNodes(map, state, paramArea, !editControls);
    }
    
    private Optional<Module> getSelectedModule() {
        return Optional.ofNullable(layers.getSelectionModel().getSelectedItem());
    }
    
    private Optional<Effect> getSelectedEffect() {
        return Optional.ofNullable(effects.getSelectionModel().getSelectedItem());
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
    
    private Optional<WritableImage> renderRaw() {
        if (state.getTemplate() == null) return Optional.empty();
        return Util.renderImage(canvas, state.getTemplate().getModules());
    }
    
    private Optional<BufferedImage> renderPNG() {
        if (state.getTemplate() == null) return Optional.empty();
        return Util.renderImage(canvas, state.getTemplate().getModules()).map(image -> SwingFXUtils.fromFXImage(image, null));
    }
    
    private void exportImage(File out) {
        if (state.getTemplate() == null) return;
        Optional<BufferedImage> img = renderPNG();
        if (!img.isPresent()) {
            Dialogs.show("There are currently invalid parameters. Unable to export image.", null, AlertType.WARNING);
            return;
        }
        try {
            BufferedImage bi = img.get();
            int i = out.getName().lastIndexOf('.');
            String format = out.getName().substring(i + 1);
            if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                bi = Util.fixJPG(bi);
            }
            boolean png = ImageIO.write(bi, format, out);
            if (!png) {
                Dialogs.show("Invalid file extension.", null, AlertType.WARNING);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.exception("Unable to export image.", null, e);
        }
        state.render();
        Dialogs.show("Exported " + out.getName(), null, AlertType.INFORMATION);
    }
    
    private void addModule(Module module) {
        module.setEditor(editControls);
        layers.getItems().add(0, module);
        canvas.addLayer();
        state.render();
    }
    
    private void addEffect(Effect effect) {
        effect.setEditor(editControls);
        getSelectedModule().ifPresent(module -> module.addEffect(effect));
        state.render();
    }
}
