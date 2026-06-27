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

package net.frozenblock.lib.block.api.waterlike;

import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.frozenblock.lib.registry.FrozenLibFabricRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public final class WaterLikeTypes {

	public static List<WaterLikeType> getAll(RegistryAccess registryAccess) {
		return registryAccess.lookupOrThrow(FrozenLibFabricRegistries.WATER_LIKE_TYPE).stream().toList();
	}

	public static List<WaterLikeType> getAllInside(Entity entity) {
		return getAll(entity.registryAccess()).stream().filter(entity::frozenLib$wasInWaterLike).toList();
	}

	public static Optional<WaterLikeType> getRandomInside(Entity entity) {
		return Util.getRandomSafe(getAllInside(entity), entity.getRandom());
	}

	public static List<WaterLikeType> getPlayerAllInside(Player player) {
		return getAll(player.registryAccess()).stream().filter(player::frozenLib$wasPlayerInWaterLike).toList();
	}

	public static Optional<WaterLikeType> getPlayerRandomInside(Player player) {
		return Util.getRandomSafe(getPlayerAllInside(player), player.getRandom());
	}

	public static List<WaterLikeType> getAllTouching(Entity entity) {
		return getAll(entity.registryAccess()).stream().filter(entity::frozenLib$wasTouchingWaterLike).toList();
	}

	public static Optional<WaterLikeType> getRandomTouching(Entity entity) {
		return Util.getRandomSafe(getAllTouching(entity), entity.getRandom());
	}

	public static List<WaterLikeType> getAllTouchingOrUnderWaterAndWaterLike(Entity entity) {
		return getAll(entity.registryAccess()).stream().filter(entity::frozenLib$isTouchingWaterLikeOrUnderWaterAndWaterLike).toList();
	}

	public static Optional<WaterLikeType> getRandomTouchingOrUnderWaterAndWaterLike(Entity entity) {
		return Util.getRandomSafe(getAllTouching(entity), entity.getRandom());
	}

	public static ResourceKey<WaterLikeType> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibFabricRegistries.WATER_LIKE_TYPE, id);
	}

	public static void register(
		BootstrapContext<WaterLikeType> context,
		ResourceKey<WaterLikeType> name,
		HolderSet<Block> blocks,
		Holder<SoundEvent> genericSwimSound,
		Holder<SoundEvent> hostileSwimSound,
		Holder<SoundEvent> playerSwimSound,
		Holder<SoundEvent> genericSplashSound,
		Holder<SoundEvent> hostileSplashSound,
		Holder<SoundEvent> playerSplashSound,
		Holder<SoundEvent> playerSplashHighSpeedSound,
		Holder<SoundEvent> enterSound,
		Holder<SoundEvent> exitSound,
		Holder<SoundEvent> ambientSound
	) {
		context.register(
			name,
			new WaterLikeType(
				blocks,
				genericSwimSound,
				hostileSwimSound,
				playerSwimSound,
				genericSplashSound,
				hostileSplashSound,
				playerSplashSound,
				playerSplashHighSpeedSound,
				enterSound,
				exitSound,
				ambientSound
			)
		);
	}
}
