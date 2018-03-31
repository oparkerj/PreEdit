package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.input.*;
import com.ssplugins.preedit.nodes.EditorCanvas;
import com.ssplugins.preedit.nodes.ResizeHandle;
import com.ssplugins.preedit.util.Util;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TextModule extends Module {
	
	private Text text;
	private ObjectProperty<Font> font;
	private FontWeight weight;
	private FontPosture posture;
	
	private void update(String family, FontWeight weight, FontPosture posture, double size) {
		font.set(Font.font(family, weight, posture, size));
	}
	
	@Override
	protected void preload() {
		text = new Text();
		font = new SimpleObjectProperty<>(Font.getDefault());
		weight = FontWeight.NORMAL;
		posture = FontPosture.REGULAR;
	}
	
	@Override
	public String getName() {
		return "Text";
	}
	
	@Override
	public void draw(Canvas canvas, GraphicsContext context) throws SilentFailException {
		String content = getInputs().getValue("Content", TextAreaInput.class);
		String family = ChoiceInput.getChoice(getInputs(), "Family", String.class);
		FontWeight weight = ChoiceInput.getChoice(getInputs(), "Weight", FontWeight.class);
		FontPosture posture = ChoiceInput.getChoice(getInputs(), "Posture", FontPosture.class);
		double size = getInputs().getValue("Size", NumberInput.class).doubleValue();
		Color color = getInputs().getValue("Color", ColorInput.class);
		Bounds bounds = getInputs().getValue("Location", LocationInput.class);
		EditorCanvas.rotate(context, Util.centerX(bounds), Util.centerY(bounds), bounds.getMinZ());
		context.setFont(Font.font(family, weight, posture, size));
		context.setFill(color);
		context.fillText(content, bounds.getMinX(), bounds.getMinY() + size);
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		TextAreaInput content = new TextAreaInput(true);
		text.textProperty().bind(content.textProperty());
		text.fontProperty().bind(font);
		map.addInput("Content", content);
		ChoiceInput<String> fontFamily = new ChoiceInput<>(Font.getFamilies(), font.get().getName(), TextInput.stringConverter());
		fontFamily.valueProperty().addListener((observable, oldValue, newValue) -> {
			update(newValue, weight, posture, font.get().getSize());
		});
		map.addInput("Family", fontFamily);
		ChoiceInput<FontWeight> fontWeight = new ChoiceInput<>(FontWeight.values(), FontWeight.NORMAL, Util.enumConverter(FontWeight.class));
		fontWeight.setCellFactory(Util.enumCellFactory());
		fontWeight.valueProperty().addListener((observable, oldValue, newValue) -> {
			weight = newValue;
			update(font.get().getFamily(), newValue, posture, font.get().getSize());
		});
		map.addInput("Weight", fontWeight);
		ChoiceInput<FontPosture> fontPosture = new ChoiceInput<>(FontPosture.values(), FontPosture.REGULAR, Util.enumConverter(FontPosture.class));
		fontPosture.setCellFactory(Util.enumCellFactory());
		fontPosture.valueProperty().addListener((observable, oldValue, newValue) -> {
			posture = newValue;
			update(font.get().getFamily(), weight, newValue, font.get().getSize());
		});
		map.addInput("Posture", fontPosture);
		NumberInput fontSize = new NumberInput(true);
		fontSize.setValue(Font.getDefault().getSize());
		fontSize.numberProperty().addListener((observable, oldValue, newValue) -> {
			update(font.get().getFamily(), weight, posture, newValue.doubleValue());
		});
		map.addInput("Size", fontSize);
		ColorInput color = new ColorInput();
		color.setValue(Color.BLACK);
		map.addInput("Color", color);
		map.addInput("Location", new LocationInput(false, true));
	}
	
	@Override
	public void linkResizeHandle(ResizeHandle handle) {
		getInputs().getInput("Location", LocationInput.class).ifPresent(handle::link);
		handle.link(text.layoutBoundsProperty());
	}
	
}
