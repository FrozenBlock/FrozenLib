package net.frozenblock.lib.config.api.entry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import java.lang.reflect.Type;

public class TypedEntry<T> {

	private final TypedEntryType<T> type;

	private final T defaultValue;

	private T value;

	public TypedEntry(TypedEntryType<T> type, T defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;

		this.value = defaultValue;
	}

	public TypedEntryType<T> getType() {
		return this.type;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public static class Serializer<T> implements JsonSerializer<TypedEntry<T>>, JsonDeserializer<TypedEntry<T>> {

		@Override
		public TypedEntry<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonObject()) {
				JsonObject object = json.getAsJsonObject();
				if (object == null) {
					return null;
				} else {
					for (var entry : ConfigRegistry.getAll()) {
						var codec = entry.codec();
						var result = codec.decode(JsonOps.INSTANCE, object);

						if (result.error().isPresent()) {
							continue;
						}

						var optional = result.result();
						if (optional.isPresent()) {
							Pair<TypedEntry<T>, JsonElement> type = (Pair<TypedEntry<T>, JsonElement>) optional.get();
							return type.getFirst();
						}
					}
				}
			}
			return null;
		}

		@Override
		public JsonElement serialize(TypedEntry<T> src, Type typeOfSrc, JsonSerializationContext context) {
			if (src != null) {
				var type = src.getType();
				if (type != null) {
					var codec = type.codec();
					if (codec != null) {
						var encoded = codec.encodeStart(JsonOps.INSTANCE, src.getValue());
						if (encoded != null && encoded.error().isEmpty()) {
							var optional = encoded.result();
							if (optional.isPresent()) {
								return optional.get();
							}
						}
					}
				}
			}
			return JsonOps.INSTANCE.empty();
		}
	}
}
