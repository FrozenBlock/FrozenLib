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

package net.frozenblock.lib.levelgen.feature.impl.stateproviders;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.feature.api.stateproviders.LeafLitterStateProvider;
import net.frozenblock.lib.levelgen.feature.api.stateproviders.StrictRuleBasedStateProvider;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibBlockStateProviderTypes {

	public static void init() {
		final var register = FrozenDeferredRegister.create(
			Registries.BLOCK_STATE_PROVIDER_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("strict_rule_based_state_provider", () -> StrictRuleBasedStateProvider.CODEC);
		register.register("leaf_litter_provider", () -> LeafLitterStateProvider.CODEC);

		register.register();
	}
}
