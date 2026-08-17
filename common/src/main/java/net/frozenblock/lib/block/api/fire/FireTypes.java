/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.api.fire;

import java.util.Collection;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class FireTypes {
	public static final ResourceKey<FireType> FIRE = createKey(FrozenLibConstants.id("fire"));
	public static final ResourceKey<FireType> DEFAULT = FIRE;

	/**
	 * @return the {@link FireType} (in {@link Holder} form) with the given {@link ResourceKey}, if available.
	 */
	public static Optional<? extends Holder<FireType>> get(RegistryAccess registryAccess, ResourceKey<FireType> id) {
		return registryAccess.lookup(FrozenLibRegistries.FIRE_TYPE).flatMap(registry -> registry.get(id));
	}

	/**
	 * @param ignoreEnabled whether to ignore the result of {@link FireType#isEnabled()} for this search.
	 * @return the {@link FireType} (in {@link Holder} form) for the given {@link Block}, if available.
	 * <p>
	 * Determined by {@link FireType.SourceSettings#fireSourceBlocks()}.
	 */
	public static Optional<Holder<FireType>> getTypeHolderForBlock(RegistryAccess registryAccess, Block block, boolean ignoreEnabled) {
		return registryAccess.lookup(FrozenLibRegistries.FIRE_TYPE)
			.flatMap(registry -> registry.stream()
				.filter(fireType -> (ignoreEnabled || fireType.isEnabled()) && fireType.sourceSettings().fireSourceBlocks().contains(block.builtInRegistryHolder()))
				.findFirst()
				.map(registry::wrapAsHolder)
			);
	}

	/**
	 * @param ignoreEnabled whether to ignore the result of {@link FireType#isEnabled()} for this search.
	 * @return the {@link FireType} (in {@link ResourceKey} form) for the given {@link Block}, if available.
	 * <p>
	 * Determined by {@link FireType.SourceSettings#fireSourceBlocks()}.
	 */
	public static Optional<ResourceKey<FireType>> getTypeKeyForBlock(RegistryAccess registryAccess, Block block, boolean ignoreEnabled) {
		return getTypeHolderForBlock(registryAccess, block, ignoreEnabled).flatMap(Holder::unwrapKey);
	}

	/**
	 * @param useMobEffects whether {@link MobEffectInstance}s will be taken into account.
	 * @return the {@link FireType} (in {@link Holder} form) for the given {@link Entity}, if available.
	 * <p>
	 * Initially determined by {@link FireType.SpreadSettings#alwaysApplyToEntityTypes() alwaysApplyToEntityTypes}.
	 * <p>
	 * If {@code useMobEffects} is {@code true}, the total power of
	 * {@link FireType.SpreadSettings#alwaysApplyToMobEffects() alwaysApplyToMobEffects} and {@link FireType.SpreadSettings#cannotApplyToMobEffects() cannotApplyToMobEffects}
	 * will be compared.
	 * <p>
	 * If the power of {@link FireType.SpreadSettings#cannotApplyToMobEffects() cannotApplyToMobEffects} is greater than or equal to that of
	 * {@link FireType.SpreadSettings#alwaysApplyToMobEffects() alwaysApplyToMobEffects}, the {@link FireType} will not be applied.
	 * <p>
	 * If no {@link MobEffectInstance}-specific {@link FireType}s are chosen, the initial {@link FireType} will be used.
	 */
	public static Optional<Holder<FireType>> getTypeHolderForEntity(Entity entity, boolean useMobEffects) {
		final Optional<Holder<FireType>> entityFireType = entity.registryAccess().lookup(FrozenLibRegistries.FIRE_TYPE)
			.flatMap(registry -> registry.stream()
				.filter(fireType -> fireType.isEnabled() && entity.is(fireType.spreadSettings().alwaysApplyToEntityTypes()))
				.findFirst()
				.map(registry::wrapAsHolder)
			);
		if (!(entity instanceof LivingEntity livingEntity) || !useMobEffects) return entityFireType;

		final Collection<MobEffectInstance> activeEffects = livingEntity.getActiveEffects();
		final Optional<Holder<FireType>> mobEffectFireType = livingEntity.registryAccess().lookup(FrozenLibRegistries.FIRE_TYPE)
			.flatMap(registry -> registry.stream()
				.filter(fireType -> {
					if (!fireType.isEnabled()) return false;

					final FireType.SpreadSettings spreadSettings = fireType.spreadSettings();
					if (spreadSettings.cannotApplyToEntityTypes().contains(entity.typeHolder())) return false;

					final int cannotApplyToEffectStrength = activeEffects.stream()
						.filter(effect -> spreadSettings.cannotApplyToMobEffects().contains(effect.getEffect()))
						.mapToInt(effect -> effect.getAmplifier() + 1)
						.sum();
					final int alwaysApplyToEffectStrength = activeEffects.stream()
						.filter(effect -> spreadSettings.alwaysApplyToMobEffects().contains(effect.getEffect()))
						.mapToInt(effect -> effect.getAmplifier() + 1)
						.sum();

					if (cannotApplyToEffectStrength >= alwaysApplyToEffectStrength) return false;
					return alwaysApplyToEffectStrength >= 1;
				})
				.findFirst()
				.map(registry::wrapAsHolder)
			);
		return mobEffectFireType.or(() -> entityFireType);
	}

	/**
	 * @param useMobEffects whether {@link MobEffectInstance}s will be taken into account.
	 * @return the {@link FireType} (in {@link ResourceKey} form) for the given {@link Entity}, if available.
	 * <p>
	 * Initially determined by {@link FireType.SpreadSettings#alwaysApplyToEntityTypes() alwaysApplyToEntityTypes}.
	 * <p>
	 * If {@code useMobEffects} is {@code true}, the total power of
	 * {@link FireType.SpreadSettings#alwaysApplyToMobEffects() alwaysApplyToMobEffects} and {@link FireType.SpreadSettings#cannotApplyToMobEffects() cannotApplyToMobEffects}
	 * will be compared.
	 * <p>
	 * If the power of {@link FireType.SpreadSettings#cannotApplyToMobEffects() cannotApplyToMobEffects} is greater than or equal to that of
	 * {@link FireType.SpreadSettings#alwaysApplyToMobEffects() alwaysApplyToMobEffects}, the {@link FireType} will not be applied.
	 * <p>
	 * If no {@link MobEffectInstance}-specific {@link FireType}s are chosen, the initial {@link FireType} will be used.
	 */
	public static Optional<ResourceKey<FireType>> getTypeKeyForEntity(Entity entity, boolean useMobEffects) {
		return getTypeHolderForEntity(entity, useMobEffects).flatMap(Holder::unwrapKey);
	}

	/**
	 * @return the {@link ResourceKey} of the current {@link FireType} attached to the given {@link Entity}, if available.
	 */
	public static Optional<ResourceKey<FireType>> getTypeFromEntity(Entity entity) {
		final FireData fireData = FireData.ATTACHMENT.get(entity);
		if (fireData != null) return fireData.type().unwrapKey();
		return Optional.empty();
	}

	/**
	 * @return the {@link ResourceKey} of the current {@link FireType} attached to the given {@link Entity}, if available.
	 * <p>
	 * If the {@link Entity} does not have an attached {@link FireType}, the {@link #DEFAULT default FireType} will be used.
	 */
	public static Optional<Holder<FireType>> getFromEntityOrDefault(Entity entity) {
		return getFromDataOrDefault(entity.registryAccess(), FireData.ATTACHMENT.get(entity));
	}

	/**
	 * @return the {@link FireType} (in {@link Holder} form) of the given {@link FireData}, if available.
	 * <p>
	 * If the {@link FireData} is {@code null}, the {@link #DEFAULT default FireType} will be used.
	 */
	public static Optional<Holder<FireType>> getFromDataOrDefault(RegistryAccess registryAccess, @Nullable FireData data) {
		return data == null
			? registryAccess.lookup(FrozenLibRegistries.FIRE_TYPE).flatMap(registry -> registry.get(DEFAULT))
			: Optional.ofNullable(data.type());
	}

	public static ResourceKey<FireType> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.FIRE_TYPE, id);
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		FireType.Builder builder
	) {
		register(context, name, builder.build());
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		FireType fireType
	) {
		context.register(name, fireType);
	}

	public static void bootstrap(BootstrapContext<FireType> context) {
		final HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
		register(
			context,
			FIRE,
			FireType.builder().fireSourceBlocks(blocks.getOrThrow(FrozenLibBlockTags.DEFAULT_FIRE_BLOCKS))
		);
	}
}
