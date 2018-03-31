package com.ssplugins.preedit.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.nodes.EditorCanvas;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Util {
	
	public static void runFXSafe(Runnable runnable) {
		if (Platform.isFxApplicationThread()) runnable.run();
		else Platform.runLater(runnable);
	}
	
	public static <T> Optional<T> runFXSafe(Callable<T> callable) {
		FutureTask<T> task = new FutureTask<>(callable);
		runFXSafe(task);
		try {
			return Optional.ofNullable(task.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	public static <T> Optional<T> runFXSafeFlat(Callable<Optional<T>> callable) {
		return runFXSafe(callable).flatMap(Function.identity());
	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
	
	public static String exceptionMessage(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
	public static Supplier<InvalidInputException> invalidInput() {
		return InvalidInputException::new;
	}
	
	public static Supplier<SilentFailException> silentFail() {
		return SilentFailException::new;
	}
	
	public static Optional<WritableImage> renderImage(EditorCanvas canvas, List<Module> modules) {
		try {
			canvas.renderImage(false, modules);
			return runFXSafe(() -> {
				WritableImage img = new WritableImage((int) canvas.getMinWidth(), (int) canvas.getMinHeight());
				SnapshotParameters sp = new SnapshotParameters();
				sp.setFill(Color.TRANSPARENT);
				canvas.snapshot(sp, img);
				return img;
			});
		} catch (SilentFailException e) {
			return Optional.empty();
		}
	}
	
	public static <T extends Enum<T>> Callback<ListView<T>, ListCell<T>> enumCellFactory() {
		return param -> new EnumCell<>();
	}
	
	private static class EnumCell<T extends Enum<T>> extends ListCell<T> {
		@Override
		protected void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText("");
				return;
			}
			setText(item.name());
		}
	}
	
	public static <T extends Enum<T>> JsonConverter<T> enumConverter(Class<T> type) {
		return new JsonConverter<T>() {
			@Override
			public JsonElement toJson(T t) {
				return new JsonPrimitive(t.name());
			}
			
			@Override
			public T fromJson(JsonElement element) {
				return T.valueOf(type, element.getAsString());
			}
		};
	}
	
	public static double centerX(Bounds bounds) {
		return bounds.getMinX() + bounds.getWidth() / 2;
	}
	
	public static double centerY(Bounds bounds) {
		return bounds.getMinY() + bounds.getHeight() / 2;
	}
	
}
