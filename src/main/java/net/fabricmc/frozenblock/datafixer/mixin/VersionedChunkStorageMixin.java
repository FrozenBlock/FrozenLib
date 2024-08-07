/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.frozenblock.datafixer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.storage.DataVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkStorage.class)
public class VersionedChunkStorageMixin {
	@WrapOperation(method = "upgradeChunkTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/DataVersion;getVersion()I"))
	private int bypassCheck(DataVersion instance, Operation<Integer> original) {
		if (!FabricDataFixesInternals.get().isEmpty()) {
			return -1;
		}

		return original.call(instance);
	}
}
