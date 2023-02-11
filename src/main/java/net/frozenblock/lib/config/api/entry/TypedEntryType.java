package net.frozenblock.lib.config.api.entry;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;

public record TypedEntryType<T>(String modId, Class<T> classType, Codec<T> codec) {

	public static final TypedEntryType<Boolean> BOOLEAN = register(new TypedEntryType<>(FrozenMain.MOD_ID, Boolean.class, Codec.BOOL));
	public static final TypedEntryType<Integer> INTEGER = register(new TypedEntryType<>(FrozenMain.MOD_ID, Integer.class, Codec.INT));
	public static final TypedEntryType<Long> LONG = register(new TypedEntryType<>(FrozenMain.MOD_ID, Long.class, Codec.LONG));
	public static final TypedEntryType<Float> FLOAT = register(new TypedEntryType<>(FrozenMain.MOD_ID, Float.class, Codec.FLOAT));
	public static final TypedEntryType<Double> DOUBLE = register(new TypedEntryType<>(FrozenMain.MOD_ID, Double.class, Codec.DOUBLE));

	public static <T> TypedEntryType<T> register(TypedEntryType<T> type) {
		return ConfigRegistry.register(type);
	}
}
