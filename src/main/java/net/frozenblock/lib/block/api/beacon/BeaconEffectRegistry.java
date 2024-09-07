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
import java.util.Collection;
import java.util.stream.Collectors;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconEffectRegistry {
	public static void register(Holder<MobEffect> effect, int tier) {
		if (tier <= 0 || tier >= 4) {
			throw new IllegalArgumentException("Attempted to register Beacon effect " + effect.unwrapKey().get().location() + " at tier " + tier + ". Tier must be between 1 and 4.");
		}

		if (BeaconBlockEntity.BEACON_EFFECTS.get(tier - 1) instanceof ArrayList<Holder<MobEffect>> arrayList) {
			arrayList.add(effect);
			BeaconBlockEntity.VALID_EFFECTS = BeaconBlockEntity.BEACON_EFFECTS.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
		} else {
			FrozenSharedConstants.LOGGER.error("Attempted to register Beacon effect " + effect.unwrapKey().get().location() + " at tier " + tier + ". Tier list is not an instance of ArrayList!");
		}
	}
}
