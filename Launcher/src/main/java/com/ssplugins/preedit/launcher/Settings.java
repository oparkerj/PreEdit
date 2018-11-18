package com.ssplugins.preedit.launcher;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

public class Settings {
    
    private Path path;
    private JsonObject json;
    
    public Settings(File file) throws IOException {
        this.path = Paths.get(file.toURI());
        if (!file.exists()) {
            json = getDefault();
            save();
        }
        JsonParser parser = new JsonParser();
        json = parser.parse(new String(Files.readAllBytes(path))).getAsJsonObject();
    }
    
    private JsonObject getDefault() {
        JsonObject json = new JsonObject();
        json.addProperty("keepVersions", 5);
        json.addProperty("autoUpdate", false);
        return json;
    }
    
    public void save() throws IOException {
        Files.write(path, new GsonBuilder().setPrettyPrinting().create().toJson(json).getBytes());
    }
    
    private <T> T get(String name, T def, Function<JsonElement, T> function) {
        if (!json.has(name)) {
            return def;
        }
        return function.apply(json.get(name));
    }
    
    public int getKeepVersions() {
        return get("keepVersions", 5, JsonElement::getAsInt);
    }
    
    public String getSkipVersion() {
        return get("skipVersion", "", JsonElement::getAsString);
    }
    
    public void setSkipVersion(Version version) {
        json.addProperty("skipVersion", version.toString());
    }
    
    public void removeSkipVersion() {
        json.remove("skipVersion");
    }
    
    public boolean getAutoUpdate() {
        return get("autoUpdate", false, JsonElement::getAsBoolean);
    }
    
    public void setAutoUpdate(boolean update) {
        json.addProperty("autoUpdate", update);
    }
    
}
