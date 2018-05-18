package com.ssplugins.preedit.edit;

import com.google.gson.*;
import com.ssplugins.preedit.adapters.EffectAdapter;
import com.ssplugins.preedit.adapters.InputMapAdapter;
import com.ssplugins.preedit.adapters.ModuleAdapter;
import com.ssplugins.preedit.adapters.TemplateAdapter;
import com.ssplugins.preedit.input.InputMap;
import com.ssplugins.preedit.util.Dialogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Catalog {
    
    private static final Path TEMPLATE_PATH = Paths.get("templates.json");
    private Gson gson;
    
    private JsonObject data;
    private Map<String, Class<? extends Module>> modules = new HashMap<>();
    private Map<String, Class<? extends Effect>> effects = new HashMap<>();
    
    private Runnable callback;
    
    public Catalog(Runnable callback) {
        this.callback = callback;
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gsonBuilder.registerTypeAdapter(Effect.class, new EffectAdapter(this));
        ModuleAdapter moduleAdapter = new ModuleAdapter(this);
        gsonBuilder.registerTypeAdapter(Module.class, moduleAdapter);
        gsonBuilder.registerTypeAdapter(NodeModule.class, moduleAdapter);
        gsonBuilder.registerTypeAdapter(InputMap.class, new InputMapAdapter());
        gsonBuilder.registerTypeAdapter(Template.class, new TemplateAdapter());
        gson = gsonBuilder.create();
        loadTemplates();
    }
    
    private static Optional<Module> instantiateModule(Class<? extends Module> aClass) {
        try {
            return Optional.ofNullable(aClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            return Optional.empty();
        }
    }
    
    private static Optional<Effect> instantiateEffect(Class<? extends Effect> aClass) {
        try {
            return Optional.of(aClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            return Optional.empty();
        }
    }
    
    private void loadTemplates() {
        try {
            JsonParser parser = new JsonParser();
            File file = new File(TEMPLATE_PATH.toUri());
            if (!file.exists()) {
                Files.write(TEMPLATE_PATH, "{}".getBytes());
                data = new JsonObject();
                return;
            }
            String json = new String(Files.readAllBytes(TEMPLATE_PATH));
            try {
                data = parser.parse(json).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                Dialogs.exception("Templates file is not valid JSON.", null, e);
            }
        } catch (IOException e) {
            Dialogs.exception("Unable to load templates.", null, e);
        }
    }
    
    private void callback() {
        if (callback != null) callback.run();
    }
    
    public Optional<JsonObject> parseJson(String json) {
        try {
            return Optional.ofNullable(new JsonParser().parse(json).getAsJsonObject());
        } catch (JsonSyntaxException e) {
            return Optional.empty();
        }
    }
    
    public Set<String> getTemplates() {
        return Collections.unmodifiableSet(data.keySet());
    }
    
    public Template newTemplate(String name, int width, int height) {
        return new Template(name, width, height);
    }
    
    private boolean addPassive(JsonObject template) {
        if (!template.has("name")) return false;
        data.add(template.get("name").getAsString(), template);
        return true;
    }
    
    public void addTemplate(JsonObject template) {
        if (addPassive(template)) callback();
    }
    
    public void mergeTemplates(JsonObject catalog) {
        catalog.keySet().forEach(s -> {
            if (templateExists(s)) return;
            addPassive(catalog.getAsJsonObject(s));
        });
        callback();
    }
    
    public boolean removeTemplate(Template template) {
        return removeTemplate(template.getName());
    }
    
    public boolean removeTemplate(String name) {
        if (templateExists(name)) {
            // TODO check if template is open. close it
            data.remove(name);
            callback();
            return true;
        }
        return false;
    }
    
    public boolean templateExists(String name) {
        return data.keySet().contains(name);
    }
    
    public Optional<Template> loadTemplate(String name) {
        if (!templateExists(name)) return Optional.empty();
        try {
            return Optional.ofNullable(gson.fromJson(data.get(name), Template.class));
        } catch (JsonSyntaxException e) {
            Dialogs.exception("Unable to load template.", null, e);
            return Optional.empty();
        }
    }
    
    public void saveTemplate(Template template) {
        data.add(template.getName(), gson.toJsonTree(template, Template.class));
        saveData();
    }
    
    public void saveData() {
        try {
            Files.write(TEMPLATE_PATH, data.toString().getBytes());
        } catch (IOException e) {
            Dialogs.exception("Unable to save template.", null, e);
        }
    }
    
    public Set<String> getModules() {
        return modules.keySet();
    }
    
    public Set<String> getEffects() {
        return effects.keySet();
    }
    
    public void registerModule(String id, Class<? extends Module> type) {
        modules.put(id, type);
    }
    
    public void registerEffect(String id, Class<? extends Effect> type) {
        effects.put(id, type);
    }
    
    public Optional<Class<? extends Module>> findModule(String name) {
        return Optional.ofNullable(modules.get(name));
    }
    
    public Optional<Class<? extends Effect>> findEffect(String name) {
        return Optional.ofNullable(effects.get(name));
    }
    
    public Optional<Module> createModule(String name) {
        return findModule(name).flatMap(Catalog::instantiateModule);
    }
    
    public Optional<Effect> createEffect(String name) {
        return findEffect(name).flatMap(Catalog::instantiateEffect);
    }
    
    public <T extends Module> Optional<T> createModule(Class<T> type) {
        return Catalog.instantiateModule(type).map(type::cast);
    }
    
    public <T extends Effect> Optional<T> createEffect(Class<T> type) {
        return Catalog.instantiateEffect(type).map(type::cast);
    }
    
}
