/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.block.api.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentEvents;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentKey;
import net.frozenblock.lib.block.impl.sound.BlockSoundSetOverride;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.sounds.BlockSoundSet;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class BlockSoundSetOverrides {
	private static final BlockAttachmentKey<List<BlockSoundSetOverride>> ATTACHMENT_KEY = BlockAttachmentKey.create(() -> "SoundTypeOverride");

	public static Optional<Holder<BlockSoundSet>> getSoundSet(BlockState state) {
		final List<BlockSoundSetOverride> overrides = state.getBlock().frozenLib$getAttached(ATTACHMENT_KEY);
		if (overrides == null) return Optional.empty();

		return overrides.stream().filter(BlockSoundSetOverride::enabled).findFirst().map(BlockSoundSetOverride::soundSet);
	}

	public static ResourceKey<BlockSoundSetOverride> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.BLOCK_SOUND_SET_OVERRIDE, id);
	}

	public static void register(
		BootstrapContext<BlockSoundSetOverride> context,
		ResourceKey<BlockSoundSetOverride> name,
		HolderSet<Block> blocks,
		Holder<BlockSoundSet> soundSet
	) {
		register(context, name, blocks, soundSet, Optional.empty());
	}

	public static void register(
		BootstrapContext<BlockSoundSetOverride> context,
		ResourceKey<BlockSoundSetOverride> name,
		HolderSet<Block> blocks,
		Holder<BlockSoundSet> soundSet,
		Holder<ConfigPredicate> configPredicate
	) {
		register(context, name, blocks, soundSet, Optional.of(configPredicate));
	}

	public static void register(
		BootstrapContext<BlockSoundSetOverride> context,
		ResourceKey<BlockSoundSetOverride> name,
		HolderSet<Block> blocks,
		Holder<BlockSoundSet> soundSet,
		Optional<Holder<ConfigPredicate>> configPredicate
	) {
		context.register(name, new BlockSoundSetOverride(blocks, soundSet, configPredicate));
	}

	@ApiStatus.Internal
	public static void init() {
		BlockAttachmentEvents.REGISTER.register((registries -> {
			registries.lookup(FrozenLibRegistries.BLOCK_SOUND_SET_OVERRIDE).ifPresent(soundTypeOverrideRegistry -> {
				soundTypeOverrideRegistry.forEach(override -> {
					override.blocks().forEach(block -> {
						final List<BlockSoundSetOverride> overrides = block.value().frozenLib$getAttachedOrDefault(ATTACHMENT_KEY, new ArrayList<>());
						overrides.add(override);
						block.value().frozenLib$setAttached(ATTACHMENT_KEY, overrides);
					});
				});
			});
		}));
	}
}
