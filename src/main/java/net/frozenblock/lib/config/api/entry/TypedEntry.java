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

	public static class Serializer implements JsonSerializer<TypedEntry>, JsonDeserializer<TypedEntry> {

		@Override
		public TypedEntry<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
							Pair<TypedEntry<?>, JsonElement> type = (Pair<TypedEntry<?>, JsonElement>) optional.get();
							return type.getFirst();
						}
					}
				}
			}
			return null;
		}

		@Override
		public JsonElement serialize(TypedEntry src, Type typeOfSrc, JsonSerializationContext context) {
			var empty = JsonOps.INSTANCE.empty();
			if (src == null) {
				return empty;
			}
			var type = src.type;
			if (type == null) {
				return empty;
			}
			var codec = type.codec();

			codec.encodeStart(JsonOps.INSTANCE, src);
			return empty;
		}
	}
}
