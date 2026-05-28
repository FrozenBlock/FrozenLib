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
import java.util.Optional;
import net.frozenblock.lib.block.impl.sound.SoundTypeOverride;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

public class SoundTypeOverrides {
	private static final ArrayList<SoundTypeOverride> SOUND_TYPE_OVERRIDES = new ArrayList<>();

	public static Optional<SoundType> getSoundType(BlockState state) {
		return SOUND_TYPE_OVERRIDES.stream()
			.filter(override -> override.matches(state))
			.findAny()
			.map(SoundTypeOverride::getSoundType);
	}

	public static ResourceKey<SoundTypeOverride> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.SOUND_TYPE_OVERRIDE, id);
	}

	public static void register(
		BootstrapContext<SoundTypeOverride> context,
		ResourceKey<SoundTypeOverride> name,
		HolderSet<Block> blocks,
		SoundType soundType
	) {
		register(context, name, blocks, soundType, Optional.empty());
	}

	public static void register(
		BootstrapContext<SoundTypeOverride> context,
		ResourceKey<SoundTypeOverride> name,
		HolderSet<Block> blocks,
		SoundType soundType,
		ConfigPredicate configPredicate
	) {
		register(context, name, blocks, soundType, Optional.of(configPredicate));
	}

	public static void register(
		BootstrapContext<SoundTypeOverride> context,
		ResourceKey<SoundTypeOverride> name,
		HolderSet<Block> blocks,
		SoundType soundType,
		Optional<ConfigPredicate> configPredicate
	) {
		context.register(name, new SoundTypeOverride(blocks, soundType, configPredicate));
	}

	@ApiStatus.Internal
	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register(registryAccess -> {
			registryAccess.lookup(FrozenLibRegistries.SOUND_TYPE_OVERRIDE).ifPresent(soundTypeOverrideRegistry -> {
				SOUND_TYPE_OVERRIDES.clear();
				soundTypeOverrideRegistry.forEach(SOUND_TYPE_OVERRIDES::add);
			});
		});
	}

}
