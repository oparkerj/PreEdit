package com.ssplugins.preedit;

import com.ssplugins.preedit.api.PreEditAPI;
import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.effects.DropShadow;
import com.ssplugins.preedit.gui.GUI;
import com.ssplugins.preedit.gui.Menu;
import com.ssplugins.preedit.modules.*;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.GridScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class PreEdit extends Application implements PreEditAPI {
	
	public static void main(String[] args) {
		Application.launch(PreEdit.class);
	}
	
    public static final String NAME = "PreEdit";
    
    private Catalog catalog;
    private Stage stage;
    private Menu menu;
	
	public PreEdit() {
		catalog = new Catalog();
		// register modules/effects
		registerModulesEffects();
	}
	
	private void registerModulesEffects() {
		catalog.registerModule("Text", TextModule.class);
		catalog.registerModule("Solid", Solid.class);
		catalog.registerModule("URLImage", URLImage.class);
		catalog.registerModule("FileImage", FileImage.class);
		catalog.registerModule("Brush", Brush.class);
		catalog.registerEffect("DropShadow", DropShadow.class);
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
			Dialogs.exception("Something went wrong.", null, e);
			Platform.exit();
		});
		stage.setTitle(NAME);
		this.menu = new Menu(this);
		GridScene menu = this.menu.getGUI();
		stage.setScene(menu);
        stage.show();
		stage.setMinWidth(stage.getWidth());
		stage.setMinHeight(stage.getHeight());
		// Make content scale with window.
		menu.get("pane", TabPane.class).ifPresent(tabPane -> {
			tabPane.prefWidthProperty().bind(stage.widthProperty());
			tabPane.prefHeightProperty().bind(stage.heightProperty());
		});
	}
    
    @Override
    public GUI getGUI() {
        return menu;
    }
    
    public Catalog getCatalog() {
        return catalog;
    }
    
    public Stage getStage() {
        return stage;
    }
    
}
