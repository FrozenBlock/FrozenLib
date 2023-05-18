/*
 * Copyright 2023 QuiltMC
 * Copyright 2023 FrozenBlock
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
import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Modified to work on Fabric
 * Original name was <STRONG>NbtHelperMixin</STRONG>
 */
@Mixin(value = DataFixTypes.class, priority = 1001)
public abstract class DataFixTypesMixin {
    @ModifyReturnValue(
            method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("RETURN")
    )
    private CompoundTag updateDataWithFixers(CompoundTag original, DataFixer fixer, CompoundTag compound,
													int oldVersion, int targetVersion) {
		var type = DataFixTypes.class.cast(this);
        return QuiltDataFixesInternals.get().updateWithAllFixers(type, original);
    }
}
