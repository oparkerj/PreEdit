package com.ssplugins.preedit.gui;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.api.PreEditTab;
import com.ssplugins.preedit.util.GridScene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

public class Menu implements GUI {
	
    private PreEdit base;
    
	private GridScene gui;
	
	private TabPane tabPane;
	private EditorTab generateTab;
	private EditorTab editTab;
	
	public Menu(PreEdit base) {
	    this.base = base;
		gui = menu(base);
	}
	
	private GridScene menu(PreEdit base) {
		GridScene gui = new GridScene(PreEdit.NAME);
		
		generateTab = new EditorTab(false, base);
		Tab tabGenerate = new Tab("Generate");
		tabGenerate.setContent(generateTab);
		editTab = new EditorTab(true, base);
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
	
	public GridScene getGUI() {
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
    
    @Override
    public void addTab(Tab tab) {
        tabPane.getTabs().add(tab);
    }
    
    @Override
	public PreEditTab getGeneratorTab() {
		return generateTab;
	}
	
	@Override
	public PreEditTab getEditorTab() {
		return editTab;
	}
	
}
