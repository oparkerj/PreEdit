package com.ssplugins.preedit;

import com.ssplugins.preedit.api.AddonLoader;
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class PreEdit extends Application implements PreEditAPI {
	
	public static void main(String[] args) {
		Application.launch(PreEdit.class);
	}
	
    public static final String NAME = "PreEdit";
	private static PreEdit instance;
    
    private Catalog catalog;
    private Stage stage;
    private Menu menu;
	
	public PreEdit() {
		catalog = new Catalog();
		registerLocalModules();
		loadAddons();
	}
	
	public static PreEdit getInstance() {
	    return instance;
    }
    
    public static Stage stage() {
	    return getInstance().stage;
    }
	
	private void registerLocalModules() {
		catalog.registerModule("Text", TextModule.class);
		catalog.registerModule("Solid", Solid.class);
		catalog.registerModule("URLImage", URLImage.class);
		catalog.registerModule("FileImage", FileImage.class);
		catalog.registerModule("Brush", Brush.class);
		catalog.registerEffect("DropShadow", DropShadow.class);
	}
	
	private void loadAddons() {
		File dir = new File("addons");
		dir.mkdirs();
		if (!dir.exists()) return;
		File[] files = dir.listFiles();
		if (files == null) return;
		URL[] urls = Stream.of(files)
						   .filter(file -> file.getName().toLowerCase().endsWith(".jar"))
						   .map(file -> {
							   try {
								   return file.toURI().toURL();
							   } catch (MalformedURLException ignored) {}
							   return null;
						   })
						   .filter(Objects::nonNull)
						   .toArray(URL[]::new);
		ClassLoader loader = URLClassLoader.newInstance(urls);
		ServiceLoader<AddonLoader> loaders = ServiceLoader.load(AddonLoader.class, loader);
		loaders.forEach(addonLoader -> addonLoader.load(this));
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
