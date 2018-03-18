package com.ssplugins.preedit;

import com.ssplugins.preedit.gui.Scenes;
import com.ssplugins.preedit.util.GUI;
import com.ssplugins.preedit.util.Util;
import javafx.application.Application;
import javafx.stage.Stage;
import org.javacord.DiscordApi;

public class PreEdit extends Application {
	
	public static void main(String[] args) {
		Application.launch(PreEdit.class);
	}
	
	public PreEdit() {
		instance = this;
	}
	
	public static final String NAME = "PreEdit";
	private static PreEdit instance;
	
	private Stage stage;
	private DiscordApi api;
	
	public static PreEdit getInstance() {
		return instance;
	}
	
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setOnCloseRequest(event -> {
			if (api != null) api.disconnect();
		});
		stage.setTitle(NAME);
		stage.setResizable(false);
		setGUI(Scenes.MENU, stage::show);
	}
	
	public void setApi(DiscordApi api) {
		this.api = api;
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
