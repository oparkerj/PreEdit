package com.ssplugins.preedit.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.edit.Template;
import com.ssplugins.preedit.util.ShiftList;

import java.lang.reflect.Type;
import java.util.List;

public class TemplateAdapter implements JsonSerializer<Template>, JsonDeserializer<Template> {
	
	private static final Type moduleType = new TypeToken<ShiftList<Module>>(){}.getType();
	
	@Override
	public Template deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = element.getAsJsonObject();
		Template template = new Template(json.get("name").getAsString());
		List<Module> modules = context.deserialize(json.getAsJsonArray("modules"), moduleType);
		modules.forEach(template::addModule);
		return template;
	}
	
	@Override
	public JsonElement serialize(Template template, Type type, JsonSerializationContext context) {
		JsonObject out = new JsonObject();
		out.addProperty("name", template.getName());
		out.add("modules", context.serialize(template.getModules(), moduleType));
		return out;
	}
	
}
