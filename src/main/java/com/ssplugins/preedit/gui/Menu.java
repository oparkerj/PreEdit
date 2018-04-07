package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.util.GUI;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.Stage;

public class Menu {
	
	private GUI gui;
	
	private TabPane tabPane;
	private EditorTab generateTab;
	private EditorTab editTab;
	
	public Menu(Stage stage) {
		gui = menu(stage);
	}
	
	private GUI menu(Stage stage) {
		GUI gui = new GUI(PreEdit.NAME);
		
		generateTab = new EditorTab(false, stage);
		Tab tabGenerate = new Tab("Generate");
		tabGenerate.setContent(generateTab);
		editTab = new EditorTab(true, stage);
		Tab tabEdit = new Tab("Edit");
		tabEdit.setContent(editTab);
		
		tabPane = new TabPane(tabGenerate, tabEdit);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.setTabMinWidth(50);
		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == tabGenerate) generateTab.updateTemplates();
		});
		gui.add("pane", 0, 0, tabPane);
		
		return gui;
	}
	
	public GUI getGUI() {
		return gui;
	}
	
	public TabPane getTabPane() {
		return tabPane;
	}
	
	public EditorTab getGenerateTab() {
		return generateTab;
	}
	
	public EditorTab getEditTab() {
		return editTab;
	}
	
}
