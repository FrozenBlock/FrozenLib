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

package net.frozenblock.lib.levelgen.blockpredicates.impl;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.blockpredicates.HasMatchingAxisPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.ConfigBlockPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.SearchInAreaBlockPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.SearchInDirectionBlockPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.TouchingBlockPredicate;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public final class FrozenLibBlockPredicateTypes {
	private static final DeferredRegister<BlockPredicateType<?>> REGISTER = DeferredRegister.create(
		Registries.BLOCK_PREDICATE_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<SearchInDirectionBlockPredicate>> SEARCH_IN_DIRECTION = register("search_in_direction", () -> SearchInDirectionBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<SearchInAreaBlockPredicate>> SEARCH_IN_AREA = register("search_in_area", () -> SearchInAreaBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<TouchingBlockPredicate>> TOUCHING = register("touching", () -> TouchingBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<ConfigBlockPredicate>> CONFIG_PREDICATE = register("config_predicate", () -> ConfigBlockPredicate.CODEC);
	public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<HasMatchingAxisPredicate>> HAS_MATCHING_AXIS = register("has_matching_axis", () -> HasMatchingAxisPredicate.CODEC);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <P extends BlockPredicate> DeferredHolder<BlockPredicateType<?>, BlockPredicateType<P>> register(String name, Supplier<MapCodec<P>> mapCodec) {
		return REGISTER.register(name, () -> mapCodec::get);
	}
}
