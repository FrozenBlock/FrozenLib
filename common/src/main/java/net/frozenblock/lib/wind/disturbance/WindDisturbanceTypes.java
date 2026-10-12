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

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.wind.disturbance.geyser.GeyserWindDisturbanceType;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

@UtilityClass
public final class WindDisturbanceTypes {
	private static final DeferredRegister<WindDisturbanceType<?>> REGISTER = DeferredRegister.create(
		FrozenLibRegistries.WIND_DISTURBANCE_TYPE_REGISTRY,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<WindDisturbanceType<?>, BreezeWindDisturbanceType> BREEZE = REGISTER.register("breeze", () -> new BreezeWindDisturbanceType());
	public static final DeferredHolder<WindDisturbanceType<?>, WindChargeWindDisturbanceType> WIND_CHARGE = REGISTER.register("wind_charge", () -> new WindChargeWindDisturbanceType());
	public static final DeferredHolder<WindDisturbanceType<?>, GeyserWindDisturbanceType> GEYSER = REGISTER.register("geyser", () -> new GeyserWindDisturbanceType());

	static {
		REGISTER.register();
	}

	public static void init() {}
}
