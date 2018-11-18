package com.ssplugins.preedit.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.ssplugins.preedit.PreEdit;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.exceptions.InvalidInputException;
import com.ssplugins.preedit.exceptions.SilentFailException;
import com.ssplugins.preedit.nodes.EditorCanvas;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
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
    
    public static void logError(Throwable throwable) {
        throwable.printStackTrace();
        File file = new File(PreEdit.getApplicationDirectory(), "errors.log");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            throwable.printStackTrace(out);
            out.println();
            out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean validURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
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
            double scaleFactor = canvas.getScaleFactor();
            canvas.scaleFactorProperty().set(1);
            canvas.renderImage(false, modules, false);
            return runFXSafe(() -> {
                WritableImage img = new WritableImage((int) canvas.getMinWidth(), (int) canvas.getMinHeight());
                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);
                canvas.snapshot(sp, img);
                canvas.scaleFactorProperty().set(scaleFactor);
                return img;
            });
        } catch (SilentFailException e) {
            return Optional.empty();
        }
    }
    
    public static WritableImage renderNode(Node node) {
        Bounds bounds = node.getLayoutBounds();
        WritableImage img = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        node.snapshot(sp, img);
        return img;
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
    
    public static JsonConverter<WritableImage> imageConverter() {
        return new JsonConverter<WritableImage>() {
            @Override
            public JsonElement toJson(WritableImage image) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
                } catch (IOException ignored) {
                }
                byte[] data = out.toByteArray();
                String d = Base64.getEncoder().encodeToString(data);
                return new JsonPrimitive(d);
            }
            
            @Override
            public WritableImage fromJson(JsonElement element) {
                byte[] data = Base64.getDecoder().decode(element.getAsString());
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                try {
                    return SwingFXUtils.toFXImage(ImageIO.read(in), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
    
    public static BufferedImage fixJPG(BufferedImage img) {
        BufferedImage fix = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        fix.createGraphics().drawImage(img, 0, 0, java.awt.Color.WHITE, null);
        return fix;
    }
    
    public static void copyToClipboard(Image img) {
        Clipboard board = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putImage(img);
        board.setContent(content);
    }
    
    public static double centerX(Bounds bounds) {
        return bounds.getMinX() + bounds.getWidth() / 2;
    }
    
    public static double centerY(Bounds bounds) {
        return bounds.getMinY() + bounds.getHeight() / 2;
    }
    
    public static <T> void softBind(Property<T> value, Property<T> property) {
        property.addListener((observable, oldValue, newValue) -> {
            value.setValue(newValue);
        });
    }
    
    public static Property<Number> bindingToProperty(NumberBinding binding) {
        ObjectProperty<Number> o = new SimpleObjectProperty<>(binding.getValue());
        binding.addListener((observable, oldValue, newValue) -> {
            o.set(newValue);
        });
        return o;
    }
    
    public static <T> void refreshList(ListView<T> view) {
        ObservableList<T> items = view.getItems();
        view.setItems(null);
        view.setItems(items);
    }
    
}
