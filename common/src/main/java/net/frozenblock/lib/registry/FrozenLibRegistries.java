package net.frozenblock.lib.registry;

import com.mojang.serialization.Lifecycle;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicateType;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class FrozenLibRegistries {

	public static final ResourceKey<Registry<ConfigPredicateType<?>>> CONFIG_PREDICATE_TYPE_REGISTRY = ResourceKey.createRegistryKey(FrozenLibConstants.id("config_predicate_type"));
	public static final MappedRegistry<ConfigPredicateType<?>> CONFIG_PREDICATE_TYPE = createSimple(CONFIG_PREDICATE_TYPE_REGISTRY, Lifecycle.stable());

	public static void init() {}

	public static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return createSimple(key, lifecycle, false, null);
	}

	public static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean synced) {
		return createSimple(key, lifecycle, synced, null);
	}

	public static <T> MappedRegistry<T> createSimple(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean synced, net.frozenblock.lib.platform.service.RegistryHelper.RegistryBootstrap<T> bootstrap) {
		return FrozenLibInitPlatformUtils.REGISTRY.createSimpleRegistry(key, lifecycle, synced, bootstrap);
	}
}
