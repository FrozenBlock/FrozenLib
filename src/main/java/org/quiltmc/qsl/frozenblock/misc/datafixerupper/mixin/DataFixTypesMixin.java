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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Modified to work on Fabric
 * Original name was <STRONG>NbtHelperMixin</STRONG>
 */
@Mixin(value = DataFixTypes.class, priority = 1001)
public class DataFixTypesMixin {

    @ModifyReturnValue(
            method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
            at = @At("RETURN")
    )
    private Dynamic updateDataWithFixers(Dynamic original, DataFixer fixer, Dynamic dynamic,
											  int oldVersion, int targetVersion) {
		var type = DataFixTypes.class.cast(this);
		var value = original.getValue();

		if (value instanceof Tag) {
			return QuiltDataFixesInternals.get().updateWithAllFixers(type, (Dynamic<Tag>) original);
		}
		return original;
	}
}
