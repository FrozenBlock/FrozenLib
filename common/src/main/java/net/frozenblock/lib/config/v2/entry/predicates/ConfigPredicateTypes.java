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

package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class ConfigPredicateTypes {

	@ApiStatus.Internal
	public static void init() {
		register("equal_to", EqualToPredicate.CODEC);
		register("greater_than", GreaterThanPredicate.CODEC);
		register("greater_than_or_equal_to", GreaterThanOrEqualToPredicate.CODEC);
		register("less_than", LessThanPredicate.CODEC);
		register("less_than_or_equal_to", LessThanOrEqualToPredicate.CODEC);
		register("all_match", AllMatchPredicate.CODEC);
		register("any_of", AnyOfPredicate.CODEC);
		register("all_of", AllOfPredicate.CODEC);
		register("not", NotPredicate.CODEC);
		register("exists", ExistsPredicate.CODEC);
		register("true", TruePredicate.CODEC);
	}

	public static <P extends ConfigPredicate> void register(Identifier id, MapCodec<P> codec) {
		Registry.register(FrozenLibRegistries.CONFIG_PREDICATE_TYPE, id, codec);
	}

	private static <P extends ConfigPredicate> void register(String name, MapCodec<P> codec) {
		register(FrozenLibConstants.id(name), codec);
	}
}
