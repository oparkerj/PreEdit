package com.ssplugins.preedit.modules;

import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.input.URLInput;
import com.ssplugins.preedit.util.Util;
import javafx.application.Platform;
import javafx.scene.image.Image;

public class URLImage extends ImageModule {
	
	@Override
	public String getName() {
		return "URLImage";
	}
	
	@Override
	protected void defineInputs(InputMap map) {
		super.defineInputs(map);
		URLInput input = new URLInput();
		input.textProperty().addListener((observable, oldValue, newValue) -> {
			String link = input.getValue().orElse(null);
			if (link == null) {
				setImage(null);
				return;
			}
			if (!Util.validURL(link)) {
				setImage(null);
				return;
			}
			new Thread(() -> {
                try {
                    Image img = new Image(link);
                    input.note(null);
                    Platform.runLater(() -> {
                        setImage(img);
                    });
                } catch (IllegalArgumentException e) {
                    input.note("Unable to get image from URL.");
                }
			}).start();
		});
		map.addInput("URL", input);
	}
	
}
