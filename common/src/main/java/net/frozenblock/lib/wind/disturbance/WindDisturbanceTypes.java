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

package net.frozenblock.lib.wind.disturbance;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.wind.disturbance.geyser.GeyserWindDisturbance;
import net.frozenblock.lib.wind.disturbance.holder.WindDisturbanceSourceType;

@UtilityClass
public final class WindDisturbanceTypes {
	private static final DeferredRegister<WindDisturbanceType<?>> REGISTER = DeferredRegister.create(
		FrozenLibRegistries.WIND_DISTURBANCE_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<WindDisturbanceType<?>, WindDisturbanceType<BreezeWindDisturbance>> BREEZE = REGISTER.register(
		"breeze",
		WindDisturbanceType.createSupplier(BreezeWindDisturbance.CODEC, BreezeWindDisturbance.STREAM_CODEC, WindDisturbanceSourceType.ENTITY)
	);
	public static final DeferredHolder<WindDisturbanceType<?>, WindDisturbanceType<WindChargeWindDisturbance>> WIND_CHARGE = REGISTER.register(
		"wind_charge",
		WindDisturbanceType.createSupplier(WindChargeWindDisturbance.CODEC, WindChargeWindDisturbance.STREAM_CODEC, WindDisturbanceSourceType.ENTITY)
	);
	public static final DeferredHolder<WindDisturbanceType<?>, WindDisturbanceType<GeyserWindDisturbance>> GEYSER = REGISTER.register(
		"geyser",
		WindDisturbanceType.createSupplier(GeyserWindDisturbance.CODEC, GeyserWindDisturbance.STREAM_CODEC, WindDisturbanceSourceType.BLOCK_ENTITY)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
