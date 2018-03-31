package com.ssplugins.preedit.util;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.nodes.NumberField;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public final class Dialogs {
	
	private static void removeHeader(Dialog dialog) {
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
	}
	
	public static Optional<ButtonType> show(String msg, String title, AlertType type) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> show(msg, title, type));
		}
		Alert alert = new Alert(type);
		alert.setTitle(title == null ? PreEdit.NAME : title);
		alert.setContentText(msg);
		return alert.showAndWait();
	}
	
	public static Optional<ButtonType> exception(String msg, String title, Throwable t) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> exception(msg, title, t));
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title == null ? PreEdit.NAME : title);
		alert.setContentText(msg);
		
		TextArea textArea = new TextArea(Util.exceptionMessage(t));
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		alert.getDialogPane().setExpandableContent(textArea);
		return alert.showAndWait();
	}
	
	public static Optional<String> input(String msg, String title) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> input(msg, title));
		}
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(title == null ? PreEdit.NAME : title);
		dialog.setContentText(msg);
		dialog.initModality(Modality.APPLICATION_MODAL);
		return dialog.showAndWait();
	}
	
	public static <T> Optional<T> choose(String msg, String title, Collection<T> choices) {
		if (choices.size() == 0) return Optional.empty();
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> choose(msg, title, choices));
		}
		ChoiceDialog<T> dialog = new ChoiceDialog<>(choices.iterator().next(), choices);
		removeHeader(dialog);
		dialog.setTitle(title == null ? PreEdit.NAME : title);
		dialog.setContentText(msg);
		dialog.initModality(Modality.APPLICATION_MODAL);
		return dialog.showAndWait();
	}
	
	public static Optional<ButtonType> saveDialog(String msg, String title) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> saveDialog(msg, title));
		}
		ButtonType dontSave = new ButtonType("Don't Save", ButtonBar.ButtonData.NO);
		ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.YES);
		Alert alert = new Alert(AlertType.CONFIRMATION, msg, dontSave, save);
		alert.setTitle(title == null ? PreEdit.NAME : title);
		alert.setContentText(msg);
		return alert.showAndWait();
	}
	
	public static Optional<File> chooseFile(Stage stage, String title, FileChooser.ExtensionFilter... filters) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> chooseFile(stage, title, filters));
		}
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title == null ? PreEdit.NAME : title);
		chooser.getExtensionFilters().addAll(filters);
		return Optional.ofNullable(chooser.showOpenDialog(stage));
	}
	
	public static Optional<File> saveFile(Stage stage, String title) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> saveFile(stage, title));
		}
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title == null ? PreEdit.NAME : title);
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PNG Files", "*.png"),
				new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg")
		);
		File file = chooser.showSaveDialog(stage);
		return Optional.ofNullable(file);
	}
	
	public static Optional<TemplateInfo> newTemplate(String title) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> newTemplate(title));
		}
		Dialog<TemplateInfo> dialog = new Dialog<>();
		dialog.setTitle(title == null ? PreEdit.NAME : title);
		dialog.setContentText("Create a new template:");
		ButtonType createButton = new ButtonType("Create", ButtonBar.ButtonData.FINISH);
		dialog.getDialogPane().getButtonTypes().add(createButton);
		
		VBox box = new VBox(5);
		HBox name = new HBox(5);
		Label labelName = new Label("Template name:");
		TextField fieldName = new TextField();
		name.getChildren().addAll(labelName, fieldName);
		HBox dims = new HBox(5);
		Label labelWidth = new Label("Dimensions:");
		NumberField fieldWidth = new NumberField(400);
		fieldWidth.setMaxWidth(75);
		Label labelHeight = new Label("x");
		NumberField fieldHeight = new NumberField(400);
		fieldHeight.setMaxWidth(75);
		dims.getChildren().addAll(labelWidth, fieldWidth, labelHeight, fieldHeight);
		box.getChildren().addAll(name, dims);
		
		Node btn = dialog.getDialogPane().lookupButton(createButton);
		btn.setDisable(true);
		fieldName.textProperty().addListener((observable, oldValue, newValue) -> {
			btn.setDisable(newValue.trim().isEmpty());
		});
		
		dialog.getDialogPane().setContent(box);
		Platform.runLater(labelName::requestFocus);
		dialog.setResultConverter(param -> {
			if (param != createButton) return null;
			try {
				int w = fieldWidth.getNumber().intValue();
				int h = fieldHeight.getNumber().intValue();
				if (w < 1 || h < 1) {
					Dialogs.show("Dimensions must be greater than 0.", null, AlertType.WARNING);
					return null;
				}
				return new TemplateInfo(fieldName.getText(), w, h);
			} catch (NumberFormatException e) {
				Dialogs.exception("Invalid dimensions.", null, e);
				return null;
			}
		});
		return dialog.showAndWait();
	}
	
}
