package net.frozenblock.lib.entity.api.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record VariantSpawnInjection(Identifier registryId, Holder variant, SpawnPrioritySelectors spawnConditions) {
	public static final MapCodec<VariantSpawnInjection> MAP_CODEC = Identifier.CODEC.fieldOf("registry").dispatchMap(
		variantSpawnInjection -> variantSpawnInjection.registryId,
		registryId ->
			RecordCodecBuilder.mapCodec(instance -> instance.group(
				instance.point(registryId),
				RegistryFixedCodec.create(ResourceKey.createRegistryKey(registryId)).fieldOf("variant").forGetter(VariantSpawnInjection::variant),
				SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(VariantSpawnInjection::spawnConditions)
			).apply(instance, VariantSpawnInjection::new))
	);
	public static final Codec<VariantSpawnInjection> CODEC = MAP_CODEC.codec();

	public VariantSpawnInjection(ResourceKey<? extends Registry<?>> registryKey, Holder<?> variant, SpawnPrioritySelectors spawnConditions) {
		this(registryKey.identifier(), variant, spawnConditions);
	}

	public VariantSpawnInjection(Object registryKey, Object variant, Object spawnConditions) {
		this((Identifier) registryKey, (Holder<?>) variant, (SpawnPrioritySelectors) spawnConditions);
	}

	public boolean matchesVariant(Object variant) {
		return variant.equals(this.variant) || variant.equals(this.variant.value()) || variant.equals(this.variant.unwrap());
	}

	public static ResourceKey<VariantSpawnInjection> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.VARIANT_SPAWN_INJECTION, id);
	}

	public static void register(
		BootstrapContext<VariantSpawnInjection> context,
		ResourceKey<VariantSpawnInjection> name,
		VariantSpawnInjection variantSpawnInjection
	) {
		context.register(name, variantSpawnInjection);
	}

	public static void register(
		BootstrapContext<VariantSpawnInjection> context,
		ResourceKey<VariantSpawnInjection> name,
		ResourceKey<? extends Registry<?>> registryKey,
		Holder<?> variant,
		SpawnPrioritySelectors spawnConditions
	) {
		register(context, name, new VariantSpawnInjection(registryKey, variant, spawnConditions));
	}

	public static void register(
		BootstrapContext<VariantSpawnInjection> context,
		Identifier name,
		ResourceKey<? extends Registry<?>> registryKey,
		Holder<?> variant,
		SpawnPrioritySelectors spawnConditions
	) {
		register(context, createKey(name), registryKey, variant, spawnConditions);
	}
}
