package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.URLInput;
import com.ssplugins.preedit.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLImage extends ImageModule {
    
    @Override
    public String getName() {
        return "URLImage";
    }
    
    public void setURL(String url) {
        getInputs().getInput("URL", URLInput.class).ifPresent(urlInput -> {
            urlInput.setValue(url);
        });
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        super.defineInputs(map);
        URLInput input = new URLInput();
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            String link = input.getValue().orElse(null);
            boolean init = input.isInit();
            if (link == null) {
                setImage(null, init);
                return;
            }
            if (!Util.validURL(link)) {
                setImage(null, init);
                return;
            }
            URLImage.this.runDelegate(() -> {
                try {
                    URLConnection conn = new URL(link).openConnection();
                    conn.setRequestProperty("User-Agent", PreEdit.NAME + " (" + PreEdit.REPO + ")");
                    try (InputStream stream = conn.getInputStream()) {
                        Image img = new Image(stream);
                        img.exceptionProperty().addListener((ob, ov, e) -> {
                            input.note("Unable to load image: " + e.getMessage());
                        });
                        Platform.runLater(() -> {
                            setImage(img, init);
                        });
                    }
                } catch (IllegalArgumentException | IOException e) {
                    input.note("Unable to get image from URL: " + e.getMessage());
                }
            });
        });
        map.addInput("URL", input);
    }
    
}
