package com.ssplugins.preedit.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ssplugins.preedit.edit.Catalog;
import com.ssplugins.preedit.edit.Effect;
import com.ssplugins.preedit.edit.Module;
import com.ssplugins.preedit.util.ShiftList;

import java.lang.reflect.Type;
import java.util.List;

public class ModuleAdapter implements JsonSerializer<Module>, JsonDeserializer<Module> {
	
	private static final Type effectType = new TypeToken<ShiftList<Effect>>(){}.getType();
	
	private Catalog catalog;
	
	public ModuleAdapter(Catalog catalog) {
		this.catalog = catalog;
	}
	
	@Override
	public Module deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = element.getAsJsonObject();
		String name = json.get("name").getAsString();
		Module module = catalog.createModule(name).orElseThrow(() -> new JsonParseException("Could not create module \"" + name + "\""));
		List<Effect> effects = context.deserialize(json.getAsJsonArray("effects"), effectType);
		effects.forEach(module::addEffect);
		JsonObject inputs = json.getAsJsonObject("inputs");
		inputs.keySet().forEach(s -> module.getInputs().getInput(s).ifPresent(input -> input.deserialize(inputs.get(s))));
		return module;
	}
	
	@Override
	public JsonElement serialize(Module module, Type type, JsonSerializationContext context) {
		JsonObject out = new JsonObject();
		out.addProperty("name", module.getName());
		out.add("effects", context.serialize(module.getEffects(), effectType));
		out.add("inputs", context.serialize(module.getInputs()));
		return out;
	}
	
}
