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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueOutput;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NbtUtils.class)
public class NbtUtilsMixin {

	@ModifyReturnValue(method = "addDataVersion(Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
	private static CompoundTag addDataVersion(CompoundTag original) {
		return QuiltDataFixesInternals.get().addModDataVersions(original);
	}

	@ModifyReturnValue(method = "addDataVersion(Lcom/mojang/serialization/Dynamic;I)Lcom/mojang/serialization/Dynamic;", at = @At("RETURN"))
	private static Dynamic<Tag> addDataVersion(Dynamic<Tag> original) {
		return new Dynamic<>(original.getOps(), QuiltDataFixesInternals.get().addModDataVersions(original.getValue().asCompound().orElseThrow()));
	}

	@Inject(method = "addDataVersion(Lnet/minecraft/world/level/storage/ValueOutput;I)V", at = @At("TAIL"))
	private static void addDataVersion(ValueOutput valueOutput, int vanillaVersion, CallbackInfo ci) {
		QuiltDataFixesInternals.get().addModDataVersions(valueOutput);
	}
}
