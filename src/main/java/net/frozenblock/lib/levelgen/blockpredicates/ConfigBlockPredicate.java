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

package net.frozenblock.lib.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.impl.FrozenLibBlockPredicateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.ApiStatus;

public class ConfigBlockPredicate implements BlockPredicate {
	public static final MapCodec<ConfigBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("config_predicate").forGetter(config -> config.configPredicate)
	).apply(instance, ConfigBlockPredicate::new));
	private final ConfigPredicate configPredicate;

	@ApiStatus.Internal
	public ConfigBlockPredicate(ConfigPredicate configPredicate) {
		this.configPredicate = configPredicate;
	}

	@Override
	public BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.CONFIG_PREDICATE;
	}

	@Override
	public boolean test(LevelAccessor level, BlockPos pos) {
		return this.configPredicate.test();
	}
}
