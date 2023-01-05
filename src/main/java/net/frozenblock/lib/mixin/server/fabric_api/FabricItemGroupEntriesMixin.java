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

package net.frozenblock.lib.mixin.server.fabric_api;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.frozenblock.lib.item.impl.FabricItemGroupAccessor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(FabricItemGroupEntries.class)
public abstract class FabricItemGroupEntriesMixin implements FabricItemGroupAccessor {
	@Shadow
	protected abstract boolean isEnabled(ItemStack stack);

	@Unique
	@Override
	public boolean enabled(ItemStack itemStack) {
		return this.isEnabled(itemStack);
	}
}
