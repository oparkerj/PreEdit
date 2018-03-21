package com.ssplugins.preedit;

import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.gui.Scenes;
import com.ssplugins.preedit.util.Dialog;
import com.ssplugins.preedit.util.GUI;
import com.ssplugins.preedit.util.Util;
import javafx.application.Application;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.Optional;

public class PreEdit extends Application {
	
	public static void main(String[] args) {
		Application.launch(PreEdit.class);
	}
	
	public PreEdit() {
		instance = this;
		catalog = new Catalog();
	}
	
	public static final String NAME = "PreEdit";
	private static PreEdit instance;
	
	private Catalog catalog;
	private Stage stage;
	
	public static PreEdit getInstance() {
		return instance;
	}
	
	public static Catalog getCatalog() {
		return getInstance().catalog;
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle(NAME);
//		stage.setResizable(false);
		GUI menu = Scenes.menu(stage);
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
