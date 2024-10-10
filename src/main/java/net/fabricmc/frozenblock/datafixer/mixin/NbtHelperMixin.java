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
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NbtUtils.class)
public class NbtHelperMixin {
	@ModifyReturnValue(method = "addDataVersion", at = @At("RETURN"))
	private static CompoundTag addModDataVersions(CompoundTag original) {
		return FabricDataFixesInternals.get().addModDataVersions(original);
	}
}
