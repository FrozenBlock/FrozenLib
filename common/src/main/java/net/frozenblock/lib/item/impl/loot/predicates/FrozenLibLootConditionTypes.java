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

package net.frozenblock.lib.item.impl.loot.predicates;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.item.api.loot.predicates.ConfigLootCondition;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

@UtilityClass
public final class FrozenLibLootConditionTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends LootItemCondition>> register = DeferredRegister.create(
			Registries.LOOT_CONDITION_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("config_predicate", () -> ConfigLootCondition.MAP_CODEC);

		register.register();
	}
}
