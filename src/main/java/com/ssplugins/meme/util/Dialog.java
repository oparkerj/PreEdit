package com.ssplugins.meme.util;

import com.ssplugins.meme.MemeBot;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;

public final class Dialog {
	
	public static Optional<ButtonType> show(String msg, String title, AlertType type) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> show(msg, title, type));
		}
		Alert alert = new Alert(type);
		alert.setTitle(title == null ? MemeBot.NAME : title);
		alert.setContentText(msg);
		return alert.showAndWait();
	}
	
	public static Optional<ButtonType> exception(String msg, String title, Throwable t) {
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> exception(msg, title, t));
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title == null ? MemeBot.NAME : title);
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
		dialog.setTitle(title == null ? MemeBot.NAME : title);
		dialog.setContentText(msg);
		dialog.initModality(Modality.APPLICATION_MODAL);
		return dialog.showAndWait();
	}
	
	public static <T> Optional<T> choose(String msg, String title, List<T> choices) {
		if (choices.size() == 0) return Optional.empty();
		if (!Platform.isFxApplicationThread()) {
			return Util.runFXSafeFlat(() -> choose(msg, title, choices));
		}
		ChoiceDialog<T> dialog = new ChoiceDialog<>(choices.get(0), choices);
		dialog.setTitle(title == null ? MemeBot.NAME : title);
		dialog.setContentText(msg);
		dialog.initModality(Modality.APPLICATION_MODAL);
		return dialog.showAndWait();
	}
	
}
