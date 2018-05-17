package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.input.FileInput;
import com.ssplugins.preedit.input.InputMap;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.File;

public class FileImage extends ImageModule {
    
    @Override
    public String getName() {
        return "FileImage";
    }
    
    public void setFile(File file) {
        getInputs().getInput("File", FileInput.class).ifPresent(fileInput -> {
            fileInput.setFile(file);
        });
    }
    
    @Override
    protected void defineInputs(InputMap map) {
        super.defineInputs(map);
        FileInput input = new FileInput();
        input.pathProperty().ifPresent(property -> property.addListener((observable, oldValue, newValue) -> {
            try {
                File f = input.getValue().orElse(null);
                boolean init = input.isInit();
                if (f == null) {
                    setImage(null, init);
                    return;
                }
                new Thread(() -> {
                    Image img = new Image("file:" + f.getAbsolutePath());
                    Platform.runLater(() -> {
                        setImage(img, init);
                    });
                }).start();
            } catch (Exception e) {
                setImage(null, false);
            }
        }));
        map.addInput("File", input);
    }
    
}
