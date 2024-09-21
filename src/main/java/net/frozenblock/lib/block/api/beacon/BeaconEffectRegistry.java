/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.block.api.beacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconEffectRegistry {
	public static void register(MobEffect effect, int tier) {
		if (tier <= 0 || tier >= 4) {
			throw new IllegalArgumentException("Attempted to register Beacon effect " + effect.getDisplayName().getString() + " at tier " + tier + ". Tier must be between 1 and 4.");
		}

		ArrayList<MobEffect> mobEffects = new ArrayList<>(Arrays.stream(BeaconBlockEntity.BEACON_EFFECTS[tier - 1]).toList());
		mobEffects.add(effect);

		BeaconBlockEntity.BEACON_EFFECTS[tier - 1] = mobEffects.toArray(new MobEffect[0]);
		BeaconBlockEntity.VALID_EFFECTS = Arrays.stream(BeaconBlockEntity.BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
	}
}
