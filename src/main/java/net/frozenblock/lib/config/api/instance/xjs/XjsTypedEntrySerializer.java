package net.frozenblock.lib.config.api.instance.xjs;

public final class XjsTypedEntrySerializer {
    private XjsTypedEntrySerializer() {}

    @SuppressWarnings("unchecked")
    public static JsonValue toJsonValue(final TypedEntry<?> src) throws NonSerializableObjectException {
        if (src != null) {
            var type = src.type();
            if (type != null) {
                var codec = type.codec();
                if (codec != null) {
                    var encoded = codec.encodeStart(XjsOps.INSTANCE, src.value());
                    if (encoded != null && encoded.error().isEmpty()) {
                        var optional = encoded.result();
                        if (optional.isPresent()) {
                            return (JsonValue) optional.get();
                        }
                    }
                }
            }
        }
        throw new NonSerializableObjectException("Failed to serialize typed entry " + src);
    }

    public static TypedEntry<?> fromJsonValue(final String modId, final JsonValue value) throws NonSerializableObjectException {
        TypedEntry<?> modEntry = getFromRegistry(json, ConfigRegistry.getTypedEntryTypesForMod(modId));
        if (modEntry != null) {
            return modEntry;
        }
        throw new NonSerializableObjectException("Failed to deserialize typed entry" + value);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <T> TypedEntry<T> getFromRegistry(final String modId, final JsonValue json, final @NotNull Collection<TypedEntryType<?>> registry) throws ClassCastException {
        for (TypedEntryType<?> entryType : registry) {
            TypedEntryType<T> newType = (TypedEntryType<T>) entryType;
            TypedEntry<T> entry = getFromType(value, newType);
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }

    @Nullable
    private <T> TypedEntry<T> getFromType(JsonValue value, @NotNull TypedEntryType<T> entryType) throws ClassCastException {
        if (!entryType.modId().equals(modId))
            return null;

        var codec = entryType.codec();
        DataResult<Pair<T, JsonValue>> result = codec.decode(XjsOps.INSTANCE, value);
        if (result.error().isPresent())
            return null;

        var optional = result.result();
        if (optional.isEmpty()) return null;

        Pair<T, JsonValue> pair = optional.get();
        T first = pair.getFirst();
        TypedEntry<T> entry = new TypedEntry<>(entryType, first);
        FrozenLogUtils.log("Built typed entry " + entry, FrozenSharedConstants.UNSTABLE_LOGGING);
        return entry;
    }
}