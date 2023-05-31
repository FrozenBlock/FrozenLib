/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.loottable.mixin;

import java.util.ArrayList;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.loottable.api.BuiltInLootTablesEntrypoint;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInLootTables.class)
public class BuiltInLootTablesMixin {

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void init(CallbackInfo info) {
		ArrayList<ResourceLocation> resourceLocationList = new ArrayList<>();
		for (EntrypointContainer<BuiltInLootTablesEntrypoint> entrypoint : FrozenMain.LOOT_TABLE_ENTRYPOINTS) {
			entrypoint.getEntrypoint().addLootTables(resourceLocationList);
		}
		for (ResourceLocation lootTableLocation : resourceLocationList) {
			register(lootTableLocation);
		}
	}

	@Shadow
	private static ResourceLocation register(ResourceLocation id) {
		throw new AssertionError("Mixin injection failed - FrozenLib BuiltInLootTablesMixin.");
	}

}
