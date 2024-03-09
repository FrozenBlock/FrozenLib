/*
 * Copyright 2023-2024 The Quilt Project
 * Copyright 2023-2024 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Modified to work on Fabric
 * Original name was <STRONG>NbtHelperMixin</STRONG>
 */
@Mixin(value = DataFixTypes.class, priority = 1001)
public class DataFixTypesMixin {

	@Shadow
	@Final
	private DSL.TypeReference type;

	@ModifyReturnValue(
            method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
            at = @At("RETURN")
    )
    private Dynamic updateDataWithFixers(Dynamic original, DataFixer fixer, Dynamic dynamic, int oldVersion, int targetVersion) {
		var type = DataFixTypes.class.cast(this);
		var value = original.getValue();

		if (value instanceof Tag && !FrozenLibConfig.get().dataFixer.disabledDataFixTypes.contains(this.type.typeName())) {
			return QuiltDataFixesInternals.get().updateWithAllFixers(type, (Dynamic<Tag>) original);
		}
		return original;
	}
}
