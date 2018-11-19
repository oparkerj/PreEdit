package com.ssplugins.preedit.input;

import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.util.calc.UndoHistory;
import com.ssplugins.preedit.util.data.JsonConverter;
import javafx.scene.control.Button;

public class ButtonInput extends Input<Button, Void> {
    
    public ButtonInput() {
        this.ready();
    }
    
    public void setButtonText(String text) {
        getNode().setText(text);
    }
    
    @Override
    protected Button createInputNode() {
        return new Button();
    }
    
    @Override
    protected Void getNodeValue(Button node) throws InvalidInputException {
        return null;
    }
    
    @Override
    protected void setNodeValue(Button node, Void value) {
        //
    }
    
    @Override
    protected boolean isValid(Void value) {
        return true;
    }
    
    @Override
    protected JsonConverter<Void> getJsonConverter() {
        return JsonConverter.empty();
    }
    
    @Override
    protected void setUpdateTrigger(Button node, Runnable update) {
        node.setOnAction(event -> update.run());
    }
    
    @Override
    protected void addUndoTrigger(UndoHistory undoHistory) {
        //
    }
    
}
