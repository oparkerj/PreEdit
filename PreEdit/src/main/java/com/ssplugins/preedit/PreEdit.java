package com.ssplugins.preedit;

import com.ssplugins.preedit.api.AddonLoader;
import com.ssplugins.preedit.api.PreEditAPI;
import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.effects.*;
import com.ssplugins.preedit.gui.EditorTab;
import com.ssplugins.preedit.gui.GUI;
import com.ssplugins.preedit.gui.Menu;
import com.ssplugins.preedit.input.FileInput;
import com.ssplugins.preedit.modules.*;
import com.ssplugins.preedit.util.Dialogs;
import com.ssplugins.preedit.util.GridScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Stream;

public class PreEdit extends Application implements PreEditAPI {
    
    public static void main(String[] args) {
        Application.launch(PreEdit.class, args);
    }
    
    public static final String NAME = "PreEdit";
    public static final String REPO = "https://github.com/567legodude/PreEdit";
    private static PreEdit instance;
    
    private Catalog catalog;
    private Stage stage;
    private Menu menu;
    
    private List<AddonLoader> addons = new ArrayList<>();
    
    public PreEdit() {
        instance = this;
        catalog = new Catalog(() -> {
            if (menu != null) getMenu().updateAll();
        });
        registerLocalModules();
    }
    
    public static PreEdit getInstance() {
        return instance;
    }
    
    public static Stage stage() {
        return getInstance().stage;
    }
    
    private void registerLocalModules() {
        catalog.registerModule("Solid", Solid.class);
        catalog.registerModule("URLImage", URLImage.class);
        catalog.registerModule("FileImage", FileImage.class);
        catalog.registerModule("Brush", Brush.class);
        catalog.registerModule("Text", TextModule.class);
        catalog.registerModule("Top Text", TopText.class);
        
        catalog.registerEffect("DropShadow", ShadowEffect.class);
        catalog.registerEffect("BoxBlur", BoxBlurEffect.class);
        catalog.registerEffect("Bloom", BloomEffect.class);
        catalog.registerEffect("ColorAdjust", ColorAdjustEffect.class);
        // TODO figure out a way to do displacement map.
        catalog.registerEffect("GaussianBlur", GaussianEffect.class);
        catalog.registerEffect("Glow", GlowEffect.class);
        catalog.registerEffect("InnerShadow", InnerShadowEffect.class);
        // TODO maybe lighting effect
        catalog.registerEffect("PerspectiveTransform", PerspectiveEffect.class);
        catalog.registerEffect("MotionBlur", MotionBlurEffect.class);
        catalog.registerEffect("Reflection", ReflectionEffect.class);
        catalog.registerEffect("Clip", ClipEffect.class);
        
        catalog.registerEffect("Scale", ScaleEffect.class);
        
//        catalog.registerEffect("BoundingBox", BoundingBoxEffect.class);
    }
    
    private List<String> loadAddons() {
        File dir = new File(getApplicationDirectory(), "addons");
        dir.mkdirs();
        if (!dir.exists()) return Collections.emptyList();
        File[] files = dir.listFiles();
        if (files == null) return Collections.emptyList();
        URL[] urls = Stream.of(files)
                           .filter(file -> file.getName().toLowerCase().endsWith(".jar"))
                           .map(file -> {
                               try {
                                   return file.toURI().toURL();
                               } catch (MalformedURLException ignored) {
                               }
                               return null;
                           })
                           .filter(Objects::nonNull).toArray(URL[]::new);
        List<String> failed = new ArrayList<>();
        ClassLoader loader = URLClassLoader.newInstance(urls);
        ServiceLoader<AddonLoader> loaders = ServiceLoader.load(AddonLoader.class, loader);
        loaders.forEach(addonLoader -> {
            String name = addonLoader.getName();
            try {
                addonLoader.load(this);
                addons.add(addonLoader);
            } catch (Throwable t) {
                failed.add(name);
            }
        });
        return failed;
    }
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            Dialogs.exception("Something went wrong.", null, e);
            Platform.exit();
        });
        stage.setOnCloseRequest(event -> {
            if (!menu.getEditTab().getState().isSaved()) {
                menu.selectTab(menu.getEditTabRaw());
                menu.getEditTab().checkSave();
            }
            addons.forEach(addonLoader -> {
                try {
                    addonLoader.onShutdown(this);
                } catch (Throwable t) {
                    Dialogs.exception("The addon '" + addonLoader.getName() + "' encountered an error while shutting down.", null, t);
                }
            });
        });
        stage.getIcons().add(new Image(PreEdit.class.getResourceAsStream("/icon.png")));
        stage.setTitle(NAME);
        this.menu = new Menu(this);
        //
        List<String> failed = loadAddons();
        if (failed.size() > 0) {
            StringBuilder builder = new StringBuilder("The following addons failed to load:");
            failed.forEach(s -> builder.append("\n").append(s));
            Dialogs.show(builder.toString(), null, Alert.AlertType.INFORMATION);
        }
        //
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
    
        loadParameters();
    }
    
    private void loadParameters() {
        List<String> params = this.getParameters().getRaw();
        if (params.size() > 0) {
            File file = new File(params.get(0));
            if (!file.exists()) {
                Dialogs.show("Input file does not exist.", null, Alert.AlertType.WARNING);
                return;
            }
            String name = file.getName().toLowerCase();
            String ext = name.substring(name.lastIndexOf('.'));
            List<String> extensions = FileInput.getExtensionFilter().getExtensions();
            if (extensions.stream().map(s -> s.substring(1)).noneMatch(s -> s.equals(ext))) {
                Dialogs.show("Input file should be one of the following formats:\n" + String.join(", ", extensions), null, Alert.AlertType.WARNING);
                return;
            }
            
            FileImage image = new FileImage();
            image.setDelegate(ImageModule.Delegate.NOT_NEXT);
            image.setFile(file);
            Platform.runLater(() -> {
                Optional<Image> img = image.getImage();
                if (!img.isPresent()) {
                    Dialogs.show("Unable to load input image.", null, Alert.AlertType.WARNING);
                    return;
                }
                Template template = new Template("", (int) img.get().getWidth(), (int) img.get().getHeight());
                template.addModule(image);
                getMenu().selectTab(getMenu().getEditTabRaw());
                EditorTab editTab = getMenu().getEditTab();
                editTab.getState().templateProperty().set(template);
                editTab.getState().upToDateProperty().set(false);
            });
        }
    }
    
    public static File getApplicationDirectory() {
        try {
            return new File(PreEdit.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            return new File(".");
        }
    }
    
    public Menu getMenu() {
        return menu;
    }
    
    @Override
    public GUI getGUI() {
        return menu;
    }
    
    @Override
    public Catalog getCatalog() {
        return catalog;
    }
    
    public Stage getStage() {
        return stage;
    }
    
}
