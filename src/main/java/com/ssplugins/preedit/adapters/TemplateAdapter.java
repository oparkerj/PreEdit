package com.ssplugins.preedit.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.util.wrapper.ShiftList;

import java.lang.reflect.Type;
import java.util.List;

public class TemplateAdapter implements JsonSerializer<Template>, JsonDeserializer<Template> {
	
	private static final Type moduleType = new TypeToken<ShiftList<Module>>(){}.getType();
	
	@Override
	public Template deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = element.getAsJsonObject();
		Template template = new Template(json.get("name").getAsString(), json.get("width").getAsInt(), json.get("height").getAsInt());
		List<Module> modules = context.deserialize(json.getAsJsonArray("modules"), moduleType);
		modules.forEach(template::addModule);
		return template;
	}
	
	@Override
	public JsonElement serialize(Template template, Type type, JsonSerializationContext context) {
		JsonObject out = new JsonObject();
		out.addProperty("name", template.getName());
		out.addProperty("width", template.getWidth());
		out.addProperty("height", template.getHeight());
        JsonArray modules = new JsonArray();
        template.getModules().forEach(module -> {
            modules.add(context.serialize(module, Module.class));
        });
		out.add("modules", modules);
		return out;
	}
	
}
