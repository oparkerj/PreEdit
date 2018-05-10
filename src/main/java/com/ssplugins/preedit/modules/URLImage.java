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
			new Thread(() -> {
                try {
                    Image img = new Image(link);
                    input.note(null);
                    Platform.runLater(() -> {
                        setImage(img, init);
                    });
                } catch (IllegalArgumentException e) {
                    input.note("Unable to get image from URL.");
                }
			}).start();
		});
		map.addInput("URL", input);
	}
	
}
