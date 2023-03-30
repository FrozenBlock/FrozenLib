package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.Marshaller;
import net.frozenblock.lib.config.api.entry.TypedEntry;

import java.util.Objects;
import java.util.function.BiFunction;

public class JanksonTypedEntrySerializer<T> implements BiFunction<TypedEntry, Marshaller, JsonElement> {

	private final String modId;

	public JanksonTypedEntrySerializer(String modId) {
		this.modId = modId;
	}

	/**
	 * Serializes a {@link TypedEntry} to a {@link JsonElement}.
	 */
	@Override
	public JsonElement apply(TypedEntry src, Marshaller marshaller) {
		if (src != null) {
			var type = src.type();
			if (type != null) {
				if (Objects.equals(type.modId(), this.modId) || Objects.equals(type.modId(), TypedEntry.DEFAULT_MOD_ID)) {
					var codec = type.codec();
					if (codec != null) {
						var encoded = codec.encodeStart(JanksonOps.INSTANCE, src.value());
						if (encoded != null && encoded.error().isEmpty()) {
							var optional = encoded.result();
							if (optional.isPresent()) {
								return (JsonElement) optional.get();
							}
						}
					}
				}
			}
		}
		return new JsonObject();
	}
}
