/*
 * Copyright (c) 2024 FabricMC
 * Copyright (c) 2024 FrozenBlock
 * Modified to use Mojang's Official Mappings
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
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

package net.fabricmc.frozenblock.datafixer.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataFixTypes.class)
public class DataFixTypesMixin {
	// From QSL.
	@SuppressWarnings({"rawtypes", "unchecked"})
	@ModifyReturnValue(
			method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
			at = @At("RETURN")
	)
	private Dynamic updateDataWithFixers(Dynamic original, DataFixer fixer, Dynamic dynamic, int oldVersion, int targetVersion) {
		DataFixTypes type = DataFixTypes.class.cast(this);
		Object value = original.getValue();

		if (type != DataFixTypes.WORLD_GEN_SETTINGS && value instanceof Tag) {
			return FabricDataFixesInternals.get().updateWithAllFixers(type, (Dynamic<Tag>) original);
		}

		return original;
	}
}
