/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.api.damagesource;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;

public final class PlayerDamageSourceSounds {
	private PlayerDamageSourceSounds() {
		throw new UnsupportedOperationException("PlayerDamageSourceSounds contains only static declarations.");
	}

    private static final Map<DamageSource, ResourceLocation> DAMAGE_SOURCE_RESOURCE_LOCATION_MAP = new Object2ObjectOpenHashMap<>();
	private static final Map<ResourceLocation, SoundEvent> RESOURCE_LOCATION_SOUND_EVENT_MAP = new Object2ObjectOpenHashMap<>();
	private static final ResourceLocation DEFAULT_ID = FrozenSharedConstants.id("default_damage_source");

	public static void addDamageSound(DamageSource source, SoundEvent sound, ResourceLocation registry) {
		DAMAGE_SOURCE_RESOURCE_LOCATION_MAP.put(source, registry);
		RESOURCE_LOCATION_SOUND_EVENT_MAP.put(registry, sound);
	}

	public static SoundEvent getDamageSound(DamageSource source) {

		return DAMAGE_SOURCE_RESOURCE_LOCATION_MAP.containsKey(source) ? getDamageSound(DAMAGE_SOURCE_RESOURCE_LOCATION_MAP.get(source)) : SoundEvents.PLAYER_HURT;
	}

	public static SoundEvent getDamageSound(ResourceLocation location) {
		return RESOURCE_LOCATION_SOUND_EVENT_MAP.getOrDefault(location, SoundEvents.PLAYER_HURT);
	}

	public static ResourceLocation getDamageID(DamageSource source) {
		return DAMAGE_SOURCE_RESOURCE_LOCATION_MAP.getOrDefault(source, DEFAULT_ID);
	}

	public static boolean containsSource(DamageSource source) {
		return DAMAGE_SOURCE_RESOURCE_LOCATION_MAP.containsKey(source);
	}

	public static boolean containsSource(ResourceLocation location) {
		return RESOURCE_LOCATION_SOUND_EVENT_MAP.containsKey(location);
	}
}
