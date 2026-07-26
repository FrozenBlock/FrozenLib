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

package net.frozenblock.lib.entity.impl.variant;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entity.api.variant.CompoundCheck;
import net.frozenblock.lib.entity.api.variant.ConfigCheck;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.variant.SpawnCondition;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibSpawnConditions {
	private static final FrozenDeferredRegister<MapCodec<? extends SpawnCondition>> REGISTER = FrozenDeferredRegister.create(
		Registries.SPAWN_CONDITION_TYPE,
		FrozenLibConstants.MOD_ID
	);

	static {
		REGISTER.register("compound", () -> CompoundCheck.MAP_CODEC);
		REGISTER.register("config", () -> ConfigCheck.MAP_CODEC);
		REGISTER.register();
	}

	public static void init() {}
}
