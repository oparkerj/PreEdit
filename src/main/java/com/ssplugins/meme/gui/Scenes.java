package com.ssplugins.meme.gui;

import com.ssplugins.meme.util.Dialog;
import com.ssplugins.meme.util.GUI;
import com.ssplugins.meme.MemeBot;
import com.ssplugins.meme.util.Util;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import org.javacord.DiscordApiBuilder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Scenes {
	
	public static final GUI LOGIN = login();
	public static final GUI MENU = menu();
	
	private static GUI login() {
		GUI gui = new GUI("Login");
		gui.setPadding(10);
		gui.setGaps(10);
		
		Label labelToken = new Label("Bot Token:");
		gui.add("labelToken", 0, 0, labelToken);
		
		AtomicBoolean working = new AtomicBoolean(false);
		PasswordField fieldToken = new PasswordField();
		fieldToken.setOnKeyPressed(event -> {
			if (working.get()) return;
			if (event.getCode() == KeyCode.ENTER) {
				Optional<Button> op = gui.get("btnLogin", Button.class);
				if (!op.isPresent()) return;
				op.get().fire();
			}
		});
		gui.add("fieldToken", 1, 0, fieldToken);
		
		Button btnLogin = new Button();
		btnLogin.setText("Login");
		btnLogin.setOnAction(event -> {
			if (working.get()) return;
			working.set(true);
			gui.show("labelLogin");
			Optional<PasswordField> op = gui.get("fieldToken", PasswordField.class);
			if (!op.isPresent()) {
				Dialog.show("Unable to get bot token.", null, AlertType.ERROR);
				return;
			}
			new DiscordApiBuilder().setToken(op.get().getText()).login().thenAccept(api -> {
				Util.log("Logged in.");
				MemeBot.getInstance().setApi(api);
				MemeBot.getInstance().setGUI(MENU);
				working.set(false);
				gui.hide("labelLogin");
			}).exceptionally(throwable -> {
				Util.log("Exception logging in.");
				Dialog.exception("Invalid bot token.", null, throwable);
				working.set(false);
				gui.hide("labelLogin");
				return null;
			});
		});
		gui.add("btnLogin", 2, 0, btnLogin);
		
		Label labelLogin = new Label("Logging in...");
		labelLogin.setVisible(false);
		gui.add("labelLogin", 3, 0, labelLogin);
		
		return gui;
	}
	
	public static GUI menu() {
		GUI gui = new GUI(MemeBot.NAME);
		gui.setPadding(10);
		
		Tab tabGenerate = new Tab("Generate");
		tabGenerate.setContent(Tabs.GENERATE);
		Tab tabEdit = new Tab("Edit");
		tabEdit.setContent(Tabs.EDIT);
		Tab tabSettings = new Tab("Settings");
		tabSettings.setContent(Tabs.SETTINGS);
		
		TabPane pane = new TabPane(tabGenerate, tabEdit, tabSettings);
		pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		pane.setTabMinWidth(50);
		gui.add("pane", 0, 0, pane);
		
		return gui;
	}
	
}
