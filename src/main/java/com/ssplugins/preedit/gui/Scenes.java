package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.util.GUI;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.Stage;

public final class Scenes {
	
	public static GUI menu(Stage stage) {
		GUI gui = new GUI(PreEdit.NAME);
		//gui.setPadding(10);
		
		Tab tabGenerate = new Tab("Generate");
		//tabGenerate.setContent(Tabs.GENERATE);
		Tab tabEdit = new Tab("Edit");
		tabEdit.setContent(new EditTab(stage));
		Tab tabSettings = new Tab("Settings");
		//tabSettings.setContent(Tabs.SETTINGS);
		
		TabPane pane = new TabPane(tabGenerate, tabEdit, tabSettings);
		pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		pane.setTabMinWidth(50);
		gui.add("pane", 0, 0, pane);
		
		return gui;
	}
	
}
