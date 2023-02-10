package net.frozenblock.lib.config.api.registry;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.Config;

public class ConfigRegistry {

	private static final List<Config<?>> CONFIG_REGISTRY = new ArrayList<>();

	private static final List<TypedEntry<?>> TYPED_ENTRY_REGISTRY = new ArrayList<>();

	static {
		register(new TypedEntry<>(FrozenMain.MOD_ID, Boolean.class, Codec.BOOL));
		register(new TypedEntry<>(FrozenMain.MOD_ID, Integer.class, Codec.INT));
		register(new TypedEntry<>(FrozenMain.MOD_ID, Long.class, Codec.LONG));
		register(new TypedEntry<>(FrozenMain.MOD_ID, Float.class, Codec.FLOAT));
		register(new TypedEntry<>(FrozenMain.MOD_ID, Double.class, Codec.DOUBLE));
	}

	public static <T> Config<T> register(Config<T> config) {
		if (CONFIG_REGISTRY.contains(config)) {
			throw new IllegalStateException("Config already registered.");
		}
		CONFIG_REGISTRY.add(config);
		return config;
	}

	public static boolean contains(Config<?> config) {
		return CONFIG_REGISTRY.contains(config);
	}

	public static <T> TypedEntry<T> register(TypedEntry<T> entry) {
		if (TYPED_ENTRY_REGISTRY.contains(entry)) {
			throw new IllegalStateException("Typed entry already registered.");
		}
		TYPED_ENTRY_REGISTRY.add(entry);
		return entry;
	}

	public static boolean contains(TypedEntry<?> entry) {
		return TYPED_ENTRY_REGISTRY.contains(entry);
	}
}
