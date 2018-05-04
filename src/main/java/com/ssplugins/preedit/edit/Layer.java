package com.ssplugins.preedit.edit;

import com.ssplugins.preedit.gui.EditorTab;
import com.ssplugins.preedit.input.Input;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

import java.util.Optional;

public abstract class Layer {
 
    private boolean editor;
	private InputMap inputs = new InputMap();
	private StringProperty displayName;
	
	private ContextMenu menu;
	private MenuItem rename;
	
	protected Layer() {
        displayName = new SimpleStringProperty(getName());
        setupMenu();
		preload();
		defineInputs(inputs);
	}
	
	private void setupMenu() {
        menu = new ContextMenu();
        rename = new MenuItem("Rename");
        menu.getItems().add(rename);
    }
    
    public final void setRenameAction(EventHandler<ActionEvent> action) {
	    rename.setOnAction(action);
    }
    
    public final ContextMenu getMenu() {
	    return menu;
    }
	
	public abstract String getName();
	
	protected abstract void defineInputs(InputMap map);
	
	public void onSelectionChange(EditorTab tab, boolean selected) {}
	
	protected void preload() {}
    
    public String getDisplayName() {
        return displayName.get();
    }
    
    public StringProperty displayNameProperty() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }
    
    public int userInputs() {
		return (int) getInputs().getInputs().values().stream().filter(Input::isUserProvided).count();
	}
    
    public void setEditor(boolean editor) {
	    this.editor = editor;
    }
    
    public boolean isEditor() {
        return editor;
    }
    
    public boolean isValid() {
		return getInputs().getInputs().values().stream().allMatch(Input::isValid);
	}
    
    public final InputMap getInputs() {
		return inputs;
	}
	
	public static <T> EventHandler<ActionEvent> renameEvent(Layer layer, ListView<T> view) {
	    return event -> {
            Optional<String> op = Dialogs.input("New name:", layer.getDisplayName(), null);
            op.ifPresent(s -> {
                layer.setDisplayName(s);
                Util.refreshList(view);
            });
        };
    }
	
}
