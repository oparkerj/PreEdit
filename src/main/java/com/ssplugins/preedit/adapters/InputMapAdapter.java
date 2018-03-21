package com.ssplugins.preedit.adapters;

import com.google.gson.*;
import com.ssplugins.preedit.input.InputMap;

import java.lang.reflect.Type;

public class InputMapAdapter implements JsonSerializer<InputMap> {
	
	@Override
	public JsonElement serialize(InputMap inputMap, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject out = new JsonObject();
		inputMap.getInputs().forEach((s, input) -> {
			JsonObject d = new JsonObject();
			d.addProperty("userProvided", input.isUserProvided());
			d.add("value", input.serialize());
			out.add(s, d);
		});
		return out;
	}
	
	public static void deserialize(JsonElement element, InputMap map) {
		JsonObject json = element.getAsJsonObject();
		json.keySet().forEach(s -> {
			JsonObject d = json.getAsJsonObject(s);
			map.getInput(s).ifPresent(input -> {
				input.setUserProvided(d.get("userProvided").getAsBoolean());
				input.deserialize(d.get("value"));
			});
		});
	}
	
}
