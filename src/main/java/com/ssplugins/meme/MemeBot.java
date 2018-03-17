package com.ssplugins.meme;

import com.ssplugins.meme.common.Scenes;
import com.ssplugins.meme.util.Dialog;
import com.ssplugins.meme.util.GUI;
import com.ssplugins.meme.util.Util;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.javacord.DiscordApi;

public class MemeBot extends Application {
	
	public static void main(String[] args) {
		Application.launch(MemeBot.class);
	}
	
	public MemeBot() {
		instance = this;
	}
	
	public static final String NAME = "MemeBot";
	private static MemeBot instance;
	
	private Stage stage;
	private DiscordApi api;
	
	public static MemeBot getInstance() {
		return instance;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
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
