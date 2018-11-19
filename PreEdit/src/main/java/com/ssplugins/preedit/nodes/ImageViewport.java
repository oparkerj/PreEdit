package com.ssplugins.preedit.nodes;

import com.ssplugins.preedit.gui.EditorTab;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.Util;
import com.ssplugins.preedit.util.calc.ScrollHandler;
import com.ssplugins.preedit.util.data.Range;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;

public class ImageViewport extends BorderPane {
    
    private ToolBar toolbar;
    private Button export, copyClipboard;
    
    private ScrollPane viewport;
    private GridPane viewArea;
    private StackPane viewNodes;
    
    private ObjectProperty<Image> source;
    private ImageView imageView;
    
    public ImageViewport() {
        source = new SimpleObjectProperty<>();
        defineNodes();
    }
    
    private void defineNodes() {
        toolbar = new ToolBar();
        this.setTop(toolbar);
    
        imageView = new ImageView();
        
        export = new Button("Export");
        export.setOnAction(event -> {
            Optional<BufferedImage> bi = toBufferedImage();
            if (!bi.isPresent()) {
                Dialogs.show("Unable to export image.", null, Alert.AlertType.WARNING);
                return;
            }
            Dialogs.saveFile(export.getScene().getWindow(), null).ifPresent(file -> EditorTab.saveImage(file, bi.get()));
        });
        export.disableProperty().bind(imageView.imageProperty().isNull());
        copyClipboard = new Button("Copy to Clipboard");
        copyClipboard.setOnAction(event -> {
            Util.copyToClipboard(imageView.getImage());
    
            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.getContent().add(new Label("Copied"));
            Bounds bounds = copyClipboard.localToScreen(copyClipboard.getBoundsInLocal());
            popup.show(copyClipboard, bounds.getMaxX(), bounds.getMinY());
        });
        copyClipboard.disableProperty().bind(imageView.imageProperty().isNull());
        toolbar.getItems().addAll(export, copyClipboard);
    
        viewport = new ScrollPane();
        this.setCenter(viewport);
        
        viewArea = new GridPane();
        viewArea.setAlignment(Pos.CENTER);
        viewArea.prefWidthProperty().bind(viewport.widthProperty());
        viewArea.prefHeightProperty().bind(viewport.heightProperty());
        viewport.setContent(viewArea);
        
        viewNodes = new StackPane();
        viewArea.getChildren().add(viewNodes);
    
        Group group = new Group(imageView);
        viewNodes.getChildren().add(group);
    }
    
    public void bindOutputToSource() {
        imageProperty().bind(sourceProperty());
    }
    
    public ObjectProperty<Image> sourceProperty() {
        return source;
    }
    
    public ObjectProperty<Image> imageProperty() {
        return imageView.imageProperty();
    }
    
    public BooleanBinding imageLoadedProperty() {
        return imageProperty().isNotNull();
    }
    
    public void setImage(BufferedImage image) {
        imageProperty().set(SwingFXUtils.toFXImage(image, null));
    }
    
    public Optional<BufferedImage> toBufferedImage() {
        return Optional.ofNullable(imageView.getImage()).map(image -> SwingFXUtils.fromFXImage(image, null));
    }
    
    public StackPane getNodeStack() {
        return viewNodes;
    }
    
    public void enableImageDrag() {
        viewport.setOnDragOver(event -> {
            Dragboard board = event.getDragboard();
            if (board.hasFiles() || board.hasImage()) {
                event.acceptTransferModes(TransferMode.LINK);
            }
            else event.consume();
        });
        viewport.setOnDragDropped(event -> {
            Dragboard board = event.getDragboard();
            boolean dropped = false;
            if (board.hasImage()) {
                sourceProperty().set(board.getImage());
                dropped = true;
            }
            else if (board.hasFiles()) {
                File file = board.getFiles().get(0);
                try {
                    Image image = new Image(file.toURI().toURL().toString());
                    sourceProperty().set(image);
                } catch (MalformedURLException | IllegalArgumentException e) {
                    Dialogs.show("Unable to load the file as an image.", null, Alert.AlertType.WARNING);
                }
                dropped = true;
            }
            event.setDropCompleted(dropped);
            event.consume();
        });
    }
    
    public void enableScaling() {
        Scale scale = new Scale(1, 1);
        imageView.getTransforms().add(scale);
        ScrollHandler handler = new ScrollHandler(viewport, Range.from(0.1, 5), 0.05, 0.15);
        scale.yProperty().bind(scale.xProperty());
        scale.xProperty().bind(handler.outputProperty());
    }
    
}
