package com.ssplugins.preedit.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.Util;
import com.ssplugins.preedit.util.wrapper.GridMap;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class FileInput extends Input<GridMap, File> {
	
	Label label;
	
	public FileInput() {
		this.ready();
	}
	
	public Optional<StringProperty> pathProperty() {
		return getNode().get("label", Label.class).map(Labeled::textProperty);
	}
	
	public void setFile(File file) {
		label.setText(file.getAbsolutePath());
	}
	
	@Override
	protected GridMap createInputNode() {
		GridMap map = new GridMap();
		Button btn = new Button("Choose File");
		btn.setOnAction(event -> {
			Optional<File> op = Dialogs.chooseFile(PreEdit.stage(), null, new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
			op.ifPresent(file -> {
				label.setText(file.getAbsolutePath());
			});
		});
		map.add("button", 0, 0, btn);
		label = new Label();
		label.setPrefWidth(100);
		label.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
		map.add("label", 1, 0, label);
		return map;
	}
	
	@Override
	protected File getNodeValue(GridMap node) throws InvalidInputException {
		return node.get("label", Label.class).filter(label -> !label.getText().isEmpty()).map(label -> new File(label.getText())).orElseThrow(Util.invalidInput());
	}
	
	@Override
	protected void setNodeValue(GridMap node, File value) {
		label.setText(value.getAbsolutePath());
	}
	
	@Override
	protected boolean isValid(File value) {
		return value != null && value.isFile();
	}
	
	@Override
	protected JsonConverter<File> getJsonConverter() {
		return new JsonConverter<File>() {
			@Override
			public JsonElement toJson(File file) {
				return new JsonPrimitive(file.getAbsolutePath());
			}
			
			@Override
			public File fromJson(JsonElement element) {
				return new File(element.getAsString());
			}
		};
	}
	
	@Override
	protected void setUpdateTrigger(GridMap node, Runnable update) {
        label.textProperty().addListener(observable -> update.run());
	}
	
}
