package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.edit.NodeModule;
import com.ssplugins.preedit.input.*;
import com.ssplugins.preedit.nodes.ResizeHandle;
import com.ssplugins.preedit.util.JsonConverter;
import com.ssplugins.preedit.util.Range;
import com.ssplugins.preedit.util.Util;
import com.ssplugins.preedit.util.wrapper.FilteredObjectProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.IllegalFormatException;
import java.util.Optional;

public class TextModule extends NodeModule {
    
    private Text text;
    private Text unwrapped;
    private FilteredObjectProperty<String> outText;
    private ObjectProperty<Font> font;
    private FontWeight weight;
    private FontPosture posture;
    
    private DoubleProperty yOffset;
    
    private ChoiceInput<String> fontFamily;
    private ChoiceInput<TextAlignment> textAlignment;
    
    private void update(String name, FontWeight weight, FontPosture posture, double size) {
        font.set(Font.font(name, weight, posture, size));
    }
    
    private static boolean LOADED = false;
    
    public static void loadFonts() {
        if (LOADED) return;
        LOADED = true;
        File fonts = new File("C:" + File.separator + "Windows" + File.separator + "Fonts");
        File[] files = fonts.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (!f.getName().toLowerCase().endsWith(".ttf")) continue;
            try {
                Font.loadFont(f.toURI().toURL().toString(), 12);
            } catch (MalformedURLException ignored) {
            }
        }
    }
    
    public void setText(String text) {
        getInputs().getInput("Content", TextAreaInput.class).ifPresent(textAreaInput -> {
            textAreaInput.setValue(text);
        });
    }
    
    @Override
    protected void preload() {
        text = new Text();
        unwrapped = new Text();
        outText = new FilteredObjectProperty<>("", s -> {
            if (s == null || s.isEmpty()) {
                return Optional.of("");
            }
            try {
                Optional<String> out = getInputs().getInput("Placeholders", TextAreaInput.class).map(this::getLines).map(strings -> String.format(s, (Object[]) strings));
                if (out.isPresent()) return out;
            } catch (IllegalFormatException ignored) {}
            return Optional.of(s);
        });
        unwrapped.textProperty().bind(text.textProperty());
        unwrapped.fontProperty().bind(text.fontProperty());
        unwrapped.textAlignmentProperty().bind(text.textAlignmentProperty());
        font = new SimpleObjectProperty<>(Font.getDefault());
        yOffset = new SimpleDoubleProperty();
        loadFonts();
    }
    
    @Override
    public Node getNode() {
        return text;
    }
    
    @Override
    public void linkResizeHandle(ResizeHandle handle) {
        getInputs().getInput("Location", LocationInput.class).ifPresent(handle::link);
        handle.link(text.layoutBoundsProperty());
    }
    
    @Override
    public ObservableValue<Bounds> getBounds() {
        return text.layoutBoundsProperty();
    }
    
    @Override
    public String getName() {
        return "Text";
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        TextAreaInput content = new TextAreaInput(true);
        content.textProperty().addListener((observable, oldValue, newValue) -> {
            outText.set(newValue);
        });
        text.textProperty().bind(outText);
        text.fontProperty().bind(font);
        map.addInput("Content", content);
        TextAreaInput placeholders = new TextAreaInput(true);
        placeholders.textProperty().addListener((observable, oldValue, newValue) -> {
            outText.update();
        });
        map.addInput("Placeholders", placeholders);
        fontFamily = new ChoiceInput<>(Font.getFamilies(), font.get().getName(), JsonConverter.forString());
        fontFamily.valueProperty().addListener((observable, oldValue, newValue) -> {
            update(newValue, weight, posture, font.get().getSize());
        });
        map.addInput("Font Family", fontFamily);
        ChoiceInput<FontWeight> fontWeight = new ChoiceInput<>(FontWeight.values(), FontWeight.NORMAL, Util.enumConverter(FontWeight.class));
        fontWeight.setCellFactory(Util.enumCellFactory());
        fontWeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            weight = newValue;
            update(font.get().getFamily(), newValue, posture, font.get().getSize());
        });
        map.addInput("Font Weight", fontWeight);
        ChoiceInput<FontPosture> fontPosture = new ChoiceInput<>(FontPosture.values(), FontPosture.REGULAR, Util.enumConverter(FontPosture.class));
        fontPosture.setCellFactory(Util.enumCellFactory());
        fontPosture.valueProperty().addListener((observable, oldValue, newValue) -> {
            posture = newValue;
            update(font.get().getFamily(), weight, newValue, font.get().getSize());
        });
        map.addInput("Font Posture", fontPosture);
        NumberInput wrap = new NumberInput(true);
        text.wrappingWidthProperty().bind(wrap.numberProperty());
        wrap.setRange(Range.lowerBound(0));
        map.addInput("Wrap Width", wrap);
        NumberInput fontSize = new NumberInput(true);
        fontSize.setValue(Font.getDefault().getSize());
        fontSize.numberProperty().addListener((observable, oldValue, newValue) -> {
            update(font.get().getFamily(), weight, posture, newValue.doubleValue());
        });
        fontSize.setRange(Range.lowerBound(1));
        map.addInput("Size", fontSize);
        ColorInput color = new ColorInput();
        color.setValue(Color.BLACK);
        text.fillProperty().bind(color.valueProperty());
        map.addInput("Color", color);
        //
        textAlignment = new ChoiceInput<>(TextAlignment.values(), TextAlignment.LEFT, Util.enumConverter(TextAlignment.class));
        textAlignment.setCellFactory(Util.enumCellFactory());
        textAlignment.valueProperty().addListener((observable, oldValue, newValue) -> {
            text.setTextAlignment(newValue);
        });
        map.addInput("Alignment", textAlignment);
        LocationInput location = new LocationInput(false, true);
        unwrapped.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            yOffset.set(-newValue.getMinY());
        });
        text.xProperty().bind(location.xProperty());
        text.yProperty().bind(location.yProperty().add(yOffset));
        text.rotateProperty().bind(location.angleProperty());
        map.addInput("Location", location);
    }
    
    public ChoiceInput<TextAlignment> getTextAlignment() {
        return textAlignment;
    }
    
    public ChoiceInput<String> getFontFamily() {
        return fontFamily;
    }
    
    private String[] getLines(TextAreaInput input) {
        return input.textProperty().get().split("\\r?\\n");
    }
    
}
