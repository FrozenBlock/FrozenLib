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
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class ConfigPredicateTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends ConfigPredicate>> register = DeferredRegister.create(
			FrozenLibRegistries.CONFIG_PREDICATE_TYPE_REGISTRY,
			FrozenLibConstants.MOD_ID
		);

		register.register("equal_to", () -> EqualToPredicate.CODEC);
		register.register("greater_than", () -> GreaterThanPredicate.CODEC);
		register.register("greater_than_or_equal_to", () -> GreaterThanOrEqualToPredicate.CODEC);
		register.register("less_than", () -> LessThanPredicate.CODEC);
		register.register("less_than_or_equal_to", () -> LessThanOrEqualToPredicate.CODEC);
		register.register("all_match", () -> AllMatchPredicate.CODEC);
		register.register("any_of", () -> AnyOfPredicate.CODEC);
		register.register("all_of", () -> AllOfPredicate.CODEC);
		register.register("not", () -> NotPredicate.CODEC);
		register.register("exists", () -> ExistsPredicate.CODEC);
		register.register("selector", () -> SelectorPredicate.CODEC);
		register.register("with_fallback", () -> WithFallbackPredicate.CODEC);
		register.register("fabric", () -> FabricPredicate.CODEC);
		register.register("neoforge", () -> NeoForgePredicate.CODEC);
		register.register("mod", () -> ModPredicate.CODEC);
		register.register("true", () -> TruePredicate.CODEC);

		register.register();
	}
}
