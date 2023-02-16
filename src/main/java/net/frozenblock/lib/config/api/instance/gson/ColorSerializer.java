package net.frozenblock.lib.config.api.instance.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.awt.*;
import java.lang.reflect.Type;

public class ColorSerializer implements JsonSerializer<Color>, JsonDeserializer<Color> {
	@Override
	public JsonElement serialize(Color color, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(color.getRGB());
	}

	@Override
	public Color deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		return new Color(element.getAsInt(), true);
	}
}
