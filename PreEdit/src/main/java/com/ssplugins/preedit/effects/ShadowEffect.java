package com.ssplugins.preedit.effects;

import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.input.ChoiceInput;
import com.ssplugins.preedit.input.ColorInput;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.CanvasLayer;
import com.ssplugins.preedit.util.Range;
import com.ssplugins.preedit.util.Util;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;

public class ShadowEffect extends Effect {
    
    private DropShadow shadow;
    
    @Override
    protected void preload() {
        shadow = new DropShadow();
    }
    
    @Override
    public void reset() {
        shadow.setInput(null);
    }
    
    @Override
    public String getName() {
        return "DropShadow";
    }
    
    @Override
    public void apply(CanvasLayer canvas, Node node, boolean editor) {
        quickApply(shadow, canvas, node);
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        ChoiceInput<BlurType> blurType = new ChoiceInput<>(BlurType.values(), shadow.getBlurType(), Util.enumConverter(BlurType.class));
        blurType.setCellFactory(Util.enumCellFactory());
        shadow.blurTypeProperty().bind(blurType.valueProperty());
        map.addInput("Blur Type", blurType);
        ColorInput color = new ColorInput();
        color.setValue(shadow.getColor());
        shadow.colorProperty().bind(color.valueProperty());
        map.addInput("Color", color);
        
        map.addNumberProperty("Height", shadow.heightProperty(), Range.from(0, 255), true);
        map.addNumberProperty("Width", shadow.widthProperty(), Range.from(0, 255), true);
        map.addNumberProperty("Radius", shadow.radiusProperty(), Range.from(0, 127), true);
        map.addNumberProperty("Offset X", shadow.offsetXProperty(), null, true);
        map.addNumberProperty("Offset Y", shadow.offsetYProperty(), null, true);
        map.addNumberProperty("Spread", shadow.spreadProperty(), Range.from(0, 1), true, 0.005);
    }
    
}
