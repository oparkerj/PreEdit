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
    private Tab generateTabRaw;
    private EditorTab generateTab;
    private Tab editTabRaw;
    private EditorTab editTab;
    
    public Menu(PreEdit base) {
        this.base = base;
        gui = menu(base);
    }
    
    private GridScene menu(PreEdit base) {
        GridScene gui = new GridScene(PreEdit.NAME);
        
        generateTab = new EditorTab(false, base);
        generateTabRaw = new Tab("Generate");
        generateTabRaw.setContent(generateTab);
        editTab = new EditorTab(true, base);
        editTabRaw = new Tab("Edit");
        editTabRaw.setContent(editTab);
        
        tabPane = new TabPane(generateTabRaw, editTabRaw);
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMinWidth(50);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == generateTabRaw) generateTab.updateTemplates();
        });
        gui.add("pane", 0, 0, tabPane);
        
        return gui;
    }
    
    public void updateAll() {
        generateTab.updateTemplates();
        editTab.updateTemplates();
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
    
    public Tab getGenerateTabRaw() {
        return generateTabRaw;
    }
    
    public Tab getEditTabRaw() {
        return editTabRaw;
    }
    
    @Override
    public void selectTab(Tab tab) {
        tabPane.getSelectionModel().select(tab);
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
