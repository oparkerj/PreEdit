package com.ssplugins.preedit.util;

import com.google.gson.JsonElement;

public abstract class JsonConverter<T> {
	
	public abstract JsonElement toJson(T t);
	
	public abstract T fromJson(JsonElement element);
	
}
