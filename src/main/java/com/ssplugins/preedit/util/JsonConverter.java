package com.ssplugins.preedit.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public abstract class JsonConverter<T> {
	
	public abstract JsonElement toJson(T t);
	
	public abstract T fromJson(JsonElement element);
	
	public static JsonConverter<String> forString() {
		return new JsonConverter<String>() {
			@Override
			public JsonElement toJson(String s) {
				return new JsonPrimitive(s);
			}
			
			@Override
			public String fromJson(JsonElement element) {
				if (element.isJsonNull()) return "";
				return element.getAsString();
			}
		};
	}
	
	public static JsonConverter<Void> empty() {
		return new JsonConverter<Void>() {
			@Override
			public JsonElement toJson(Void aVoid) {
				return JsonNull.INSTANCE;
			}
			
			@Override
			public Void fromJson(JsonElement element) {
				return null;
			}
		};
	}
	
}
