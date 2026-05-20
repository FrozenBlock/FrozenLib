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

package net.frozenblock.lib.levelgen.feature.impl.blockpredicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.ConfigEntryBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.SearchInAreaBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.SearchInDirectionBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.TouchingBlockPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class FrozenLibBlockPredicateTypes {
	public static final BlockPredicateType<SearchInDirectionBlockPredicate> SEARCH_IN_DIRECTION = register("search_in_direction", SearchInDirectionBlockPredicate.CODEC);
	public static final BlockPredicateType<SearchInAreaBlockPredicate> SEARCH_IN_AREA = register("search_in_area", SearchInAreaBlockPredicate.CODEC);
	public static final BlockPredicateType<TouchingBlockPredicate> TOUCHING = register("touching", TouchingBlockPredicate.CODEC);
	public static final BlockPredicateType<ConfigEntryBlockPredicate> CONFIG_ENTRY = register("config_entry", ConfigEntryBlockPredicate.CODEC);

	public static void init() {}

	private static <P extends BlockPredicate> BlockPredicateType<P> register(String name, MapCodec<P> mapCodec) {
		return Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, FrozenLibConstants.id(name), () -> mapCodec);
	}
}
