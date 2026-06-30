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
import net.frozenblock.lib.FrozenLibConstants;
<<<<<<<< HEAD:common/src/main/java/net/frozenblock/lib/levelgen/feature/impl/blockpredicates/FrozenLibBlockPredicateTypes.java
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.ConfigBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.SearchInAreaBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.SearchInDirectionBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.TouchingBlockPredicate;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.registries.Registries;
========
import net.frozenblock.lib.levelgen.blockpredicates.HasMatchingAxisPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.ConfigBlockPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.SearchInAreaBlockPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.SearchInDirectionBlockPredicate;
import net.frozenblock.lib.levelgen.blockpredicates.TouchingBlockPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
>>>>>>>> 383b602df (blockstate property predicates):src/main/java/net/frozenblock/lib/levelgen/blockpredicates/impl/FrozenLibBlockPredicateTypes.java
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import java.util.function.Supplier;

public class FrozenLibBlockPredicateTypes {
<<<<<<<< HEAD:common/src/main/java/net/frozenblock/lib/levelgen/feature/impl/blockpredicates/FrozenLibBlockPredicateTypes.java
	private static final FrozenDeferredRegister<BlockPredicateType<?>> REGISTER = FrozenDeferredRegister.create(
		Registries.BLOCK_PREDICATE_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final FrozenHolder<BlockPredicateType<?>, BlockPredicateType<SearchInDirectionBlockPredicate>> SEARCH_IN_DIRECTION = register("search_in_direction", () -> SearchInDirectionBlockPredicate.CODEC);
	public static final FrozenHolder<BlockPredicateType<?>, BlockPredicateType<SearchInAreaBlockPredicate>> SEARCH_IN_AREA = register("search_in_area", () -> SearchInAreaBlockPredicate.CODEC);
	public static final FrozenHolder<BlockPredicateType<?>, BlockPredicateType<TouchingBlockPredicate>> TOUCHING = register("touching", () -> TouchingBlockPredicate.CODEC);
	public static final FrozenHolder<BlockPredicateType<?>, BlockPredicateType<ConfigBlockPredicate>> CONFIG_PREDICATE = register("config_predicate", () -> ConfigBlockPredicate.CODEC);

	static {
		REGISTER.register();
	}
========
	public static final BlockPredicateType<SearchInDirectionBlockPredicate> SEARCH_IN_DIRECTION = register("search_in_direction", SearchInDirectionBlockPredicate.CODEC);
	public static final BlockPredicateType<SearchInAreaBlockPredicate> SEARCH_IN_AREA = register("search_in_area", SearchInAreaBlockPredicate.CODEC);
	public static final BlockPredicateType<TouchingBlockPredicate> TOUCHING = register("touching", TouchingBlockPredicate.CODEC);
	public static final BlockPredicateType<ConfigBlockPredicate> CONFIG_PREDICATE = register("config_predicate", ConfigBlockPredicate.CODEC);
	public static final BlockPredicateType<HasMatchingAxisPredicate> HAS_MATCHING_AXIS = register("has_matching_axis", HasMatchingAxisPredicate.CODEC);
>>>>>>>> 383b602df (blockstate property predicates):src/main/java/net/frozenblock/lib/levelgen/blockpredicates/impl/FrozenLibBlockPredicateTypes.java

	public static void init() {}

	private static <P extends BlockPredicate> FrozenHolder<BlockPredicateType<?>, BlockPredicateType<P>> register(String name, Supplier<MapCodec<P>> mapCodec) {
		return REGISTER.register(name, () -> mapCodec::get);
	}
}
