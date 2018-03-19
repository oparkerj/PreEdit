package com.ssplugins.preedit.adapters;

import com.google.gson.*;
import com.ssplugins.preedit.input.InputMap;

import java.lang.reflect.Type;

public class InputMapAdapter implements JsonSerializer<InputMap> {
	
	@Override
	public JsonElement serialize(InputMap inputMap, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject out = new JsonObject();
		inputMap.getInputs().forEach((s, input) -> out.add(s, input.serialize()));
		return out;
	}
	
}
