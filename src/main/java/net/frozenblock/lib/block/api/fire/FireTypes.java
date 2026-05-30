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

import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public final class FireTypes {
	public static final ResourceKey<FireType> FIRE = createKey(FrozenLibConstants.id("fire"));
	public static final ResourceKey<FireType> SOUL_FIRE = createKey(FrozenLibConstants.id("soul_fire"));
	public static final ResourceKey<FireType> DEFAULT = FIRE;

	public static Holder<FireType> get(RegistryAccess registryAccess, ResourceKey<FireType> id) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.FIRE_TYPE).getOrThrow(id);
	}

	public static Optional<Holder<FireType>> getTypeHolderForBlock(RegistryAccess registryAccess, Block block, boolean ignoreEnabled) {
		final Registry<FireType> registry = registryAccess.lookupOrThrow(FrozenLibRegistries.FIRE_TYPE);
		for (FireType type : registry) {
			if ((ignoreEnabled || type.isEnabled()) && type.sourceSettings().fireSourceBlocks().contains(block.builtInRegistryHolder())) return Optional.of(registry.wrapAsHolder(type));
		}
		return Optional.empty();
	}

	public static Optional<ResourceKey<FireType>> getTypeKeyForBlock(RegistryAccess registryAccess, Block block, boolean ignoreEnabled) {
		return getTypeHolderForBlock(registryAccess, block, ignoreEnabled).flatMap(Holder::unwrapKey);
	}

	public static Optional<Holder<FireType>> getTypeHolderForEntity(Entity entity) {
		final Registry<FireType> registry = entity.registryAccess().lookupOrThrow(FrozenLibRegistries.FIRE_TYPE);
		for (FireType type : registry) {
			if (type.isEnabled() && type.spreadSettings().alwaysApplyToEntityTypes().contains(entity.typeHolder())) return Optional.of(registry.wrapAsHolder(type));
		}
		return Optional.empty();
	}

	public static Optional<ResourceKey<FireType>> getTypeKeyForEntity(Entity entity) {
		return getTypeHolderForEntity(entity).flatMap(Holder::unwrapKey);
	}

	public static Optional<ResourceKey<FireType>> getTypeFromEntity(Entity entity) {
		final FireData fireData = entity.getAttached(FireData.ATTACHMENT);
		if (fireData != null) return fireData.type().unwrapKey();
		return Optional.empty();
	}

	public static Holder<FireType> getFromEntityOrDefault(Entity entity) {
		return getFromDataOrDefault(entity.registryAccess(), entity.getAttached(FireData.ATTACHMENT));
	}

	public static Holder<FireType> getFromDataOrDefault(RegistryAccess registryAccess, @Nullable FireData data) {
		return data == null
			? registryAccess.lookupOrThrow(FrozenLibRegistries.FIRE_TYPE).getOrThrow(DEFAULT)
			: data.type();
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
			FireType.builder()
				.fireSourceBlocks(blocks.getOrThrow(FrozenLibBlockTags.DEFAULT_FIRE_BLOCKS))
		);
		register(
			context,
			SOUL_FIRE,
			FireType.builder()
				.fireSourceBlocks(blocks.getOrThrow(FrozenLibBlockTags.SOUL_FIRE_BLOCKS))
				.supportingBlocks(blocks.getOrThrow(BlockTags.SOUL_FIRE_BASE_BLOCKS))
				.damage(2F)
				.textures(Identifier.withDefaultNamespace("soul_fire_0"), Identifier.withDefaultNamespace("soul_fire_1"))
		);
	}
}
