/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.api.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.frozenblock.lib.levelgen.feature.impl.blockpredicates.FrozenLibBlockPredicateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.ApiStatus;

public class ConfigEntryBlockPredicate implements BlockPredicate {
	public static final MapCodec<ConfigEntryBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate)
	).apply(instance, ConfigEntryPredicate::asBlockPredicate));
	private final ConfigEntryPredicate<?> configEntryPredicate;

	@ApiStatus.Internal
	public ConfigEntryBlockPredicate(ConfigEntryPredicate<?> configEntryPredicate) {
		this.configEntryPredicate = configEntryPredicate;
	}

	@Override
	public BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.CONFIG_ENTRY;
	}

	@Override
	public boolean test(WorldGenLevel level, BlockPos pos) {
		return this.configEntryPredicate.evaluate();
	}

}
