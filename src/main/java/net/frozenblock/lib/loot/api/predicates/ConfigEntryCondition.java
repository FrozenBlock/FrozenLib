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

package net.frozenblock.lib.loot.api.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.ApiStatus;

public class ConfigEntryCondition implements LootItemCondition {
	public static final MapCodec<ConfigEntryCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate)
	).apply(instance, ConfigEntryPredicate::asLootCondition));
	private final ConfigEntryPredicate<?> configEntryPredicate;

	@ApiStatus.Internal
	public ConfigEntryCondition(ConfigEntryPredicate<?> configEntryPredicate) {
		this.configEntryPredicate = configEntryPredicate;
	}

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return MAP_CODEC;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return this.configEntryPredicate.evaluate();
	}
}
