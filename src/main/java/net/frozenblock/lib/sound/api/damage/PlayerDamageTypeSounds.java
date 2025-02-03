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

package net.frozenblock.lib.sound.api.damage;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;

@UtilityClass
public class PlayerDamageTypeSounds {
	private static final Map<DamageType, ResourceLocation> DAMAGE_TYPE_RESOURCE_LOCATION_MAP = new Object2ObjectOpenHashMap<>();
	private static final Map<ResourceLocation, SoundEvent> RESOURCE_LOCATION_SOUND_EVENT_MAP = new Object2ObjectOpenHashMap<>();
	private static final ResourceLocation DEFAULT_ID = FrozenLibConstants.id("default_damage_source");

	/**
	 * Adds a custom sound to be played upon a player getting damaged by a specific {@link DamageType}.
	 *
	 * @param type The {@link DamageType} to register the sound for.
	 * @param sound The {@link SoundEvent} to register.
	 * @param registry The {@link ResourceLocation} to register the sound override under.
	 */
	public static void addDamageSound(DamageType type, SoundEvent sound, ResourceLocation registry) {
		DAMAGE_TYPE_RESOURCE_LOCATION_MAP.put(type, registry);
		RESOURCE_LOCATION_SOUND_EVENT_MAP.put(registry, sound);
	}

	public static SoundEvent getDamageSound(DamageType type) {
		return DAMAGE_TYPE_RESOURCE_LOCATION_MAP.containsKey(type) ? getDamageSound(DAMAGE_TYPE_RESOURCE_LOCATION_MAP.get(type)) : SoundEvents.PLAYER_HURT;
	}

	public static SoundEvent getDamageSound(ResourceLocation location) {
		return RESOURCE_LOCATION_SOUND_EVENT_MAP.getOrDefault(location, SoundEvents.PLAYER_HURT);
	}

	public static ResourceLocation getDamageID(DamageType type) {
		return DAMAGE_TYPE_RESOURCE_LOCATION_MAP.getOrDefault(type, DEFAULT_ID);
	}

	public static boolean containsSource(DamageType type) {
		return DAMAGE_TYPE_RESOURCE_LOCATION_MAP.containsKey(type);
	}

	public static boolean containsSource(ResourceLocation location) {
		return RESOURCE_LOCATION_SOUND_EVENT_MAP.containsKey(location);
	}
}
