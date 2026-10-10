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

package net.frozenblock.lib.config.v2.entry.getter.floats;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;

public final class ConfigValue implements ContextFloatProvider {
	public static final MapCodec<ConfigValue> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ID.CODEC.fieldOf("entry").forGetter(provider -> provider.entryId)
	).apply(instance, ConfigValue::new));
	private final ID entryId;
	private final ConfigEntry<Float> entry;

	public ConfigValue(ID entryId) {
		this.entryId = entryId;
		this.entry = (ConfigEntry<Float>) ConfigV2Registry.getEntry(entryId);
	}

	@Override
	public float getFloatUnsafe(LootContext lootContext) throws ArithmeticException {
		return this.entry.get();
	}

	@Override
	public MapCodec<ConfigValue> codec() {
		return MAP_CODEC;
	}

	@Override
	public void validate(ValidationContext context) {}
}
