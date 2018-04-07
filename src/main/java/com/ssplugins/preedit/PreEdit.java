package com.ssplugins.preedit;

import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.effects.DropShadow;
import com.ssplugins.preedit.gui.Menu;
import com.ssplugins.preedit.modules.*;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.GUI;
import com.ssplugins.preedit.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class PreEdit extends Application {
	
	public static void main(String[] args) {
		Application.launch(PreEdit.class);
	}
	
	public PreEdit() {
		instance = this;
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
	
	public static final String NAME = "PreEdit";
	private static PreEdit instance;
	
	private Catalog catalog;
	private Stage stage;
	private Menu menu;
	
	public static PreEdit getInstance() {
		return instance;
	}
	
	public static Catalog getCatalog() {
		return getInstance().catalog;
	}
	
	public static Stage stage() {
		return getInstance().stage;
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
			Dialogs.exception("Something went wrong.", null, e);
			Platform.exit();
		});
		stage.setTitle(NAME);
		this.menu = new Menu(stage);
		GUI menu = this.menu.getGUI();
		setGUI(menu, () -> {
			stage.show();
			stage.setMinWidth(stage.getWidth());
			stage.setMinHeight(stage.getHeight());
			// Make content scale with window.
			menu.get("pane", TabPane.class).ifPresent(tabPane -> {
				tabPane.prefWidthProperty().bind(stage.widthProperty());
				tabPane.prefHeightProperty().bind(stage.heightProperty());
			});
		});
	}
	
	public void setGUI(GUI gui) {
		setGUI(gui, null);
	}
	
	public void setGUI(GUI gui, Runnable callback) {
		Runnable action = () -> {
			stage.setTitle(gui.getTitle());
			stage.setScene(gui);
			if (callback != null) callback.run();
		};
		Util.runFXSafe(action);
	}
	
}
