package net.frozenblock.lib.config.api.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import java.lang.reflect.Type;
import java.util.Objects;

public record TypedEntry<T>(TypedEntryType<T> type, T value) {

	public static class Serializer<T> implements JsonSerializer<TypedEntry<T>>, JsonDeserializer<TypedEntry<T>> {

		private final String modId;

		public Serializer(String modId) {
			this.modId = modId;
		}

		@Override
		public TypedEntry<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonObject()) {
				JsonObject object = json.getAsJsonObject();
				if (object == null) {
					return null;
				} else {
					for (var entry : ConfigRegistry.getAll()) {
						if (Objects.equals(entry.modId(), this.modId) || Objects.equals(entry.modId(), FrozenMain.MOD_ID)) {
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
			}
			return null;
		}

		@Override
		public JsonElement serialize(TypedEntry<T> src, Type typeOfSrc, JsonSerializationContext context) {
			if (src != null) {
				var type = src.type();
				if (type != null) {
					if (Objects.equals(type.modId(), this.modId) || Objects.equals(type.modId(), FrozenMain.MOD_ID)) {
						var codec = type.codec();
						if (codec != null) {
							var encoded = codec.encodeStart(JsonOps.INSTANCE, src.value());
							if (encoded != null && encoded.error().isEmpty()) {
								var optional = encoded.result();
								if (optional.isPresent()) {
									return optional.get();
								}
							}
						}
					}
				}
			}
			return JsonOps.INSTANCE.empty();
		}
	}
}
