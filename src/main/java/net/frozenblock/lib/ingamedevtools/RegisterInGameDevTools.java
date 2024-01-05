/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.ingamedevtools;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.ingamedevtools.item.Camera;
import net.frozenblock.lib.ingamedevtools.item.LootTableWhacker;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class RegisterInGameDevTools {
	public static final Item CAMERA = new Camera(new FabricItemSettings().maxCount(1));
	public static final Item LOOT_TABLE_WHACKER = new LootTableWhacker(new FabricItemSettings().maxCount(1));

	public static void register() {
		Registry.register(BuiltInRegistries.ITEM, FrozenSharedConstants.id("camera"), CAMERA);
		Registry.register(BuiltInRegistries.ITEM, FrozenSharedConstants.string("loot_table_whacker"), LOOT_TABLE_WHACKER);
	}
}
