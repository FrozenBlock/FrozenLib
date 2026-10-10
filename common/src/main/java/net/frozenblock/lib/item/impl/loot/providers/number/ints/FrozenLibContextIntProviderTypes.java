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

package net.frozenblock.lib.item.impl.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.entry.getter.ints.ConfigValue;
import net.frozenblock.lib.config.v2.entry.getter.ints.ConfigWithFallbackValue;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibContextIntProviderTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends ContextIntProvider>> register = DeferredRegister.create(
			Registries.CONTEXT_INT_PROVIDER_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("config_entry", () -> ConfigValue.MAP_CODEC);
		register.register("config_entry_with_fallback", () -> ConfigWithFallbackValue.MAP_CODEC);

		register.register();
	}
}
