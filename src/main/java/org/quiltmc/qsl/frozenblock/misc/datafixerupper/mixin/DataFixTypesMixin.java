/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
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

	@WrapOperation(
		method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/datafixers/DataFixer;update(Lcom/mojang/datafixers/DSL$TypeReference;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;"
		)
	)
	private <T> Dynamic<T> updateDataWithFixers(
		DataFixer instance,
		DSL.TypeReference typeReference,
		Dynamic<T> tDynamic,
		int oldVersion,
		int targetVersion,
		Operation<Dynamic<T>> original
	) {
		var type = DataFixTypes.class.cast(this);
		Dynamic<T> value = original.call(instance, typeReference, tDynamic, oldVersion, targetVersion);

		if (value.getValue() instanceof Tag && !FrozenLibConfig.get().dataFixer.disabledDataFixTypes.contains(typeReference.typeName())) {
			//noinspection unchecked
			return (Dynamic<T>) QuiltDataFixesInternals.get().updateWithAllFixers(typeReference, type, (Dynamic<Tag>) value);
		}
		return value;
	}
}
