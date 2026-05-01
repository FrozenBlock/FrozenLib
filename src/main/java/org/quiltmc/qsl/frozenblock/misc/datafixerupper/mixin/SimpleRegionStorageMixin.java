/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(SimpleRegionStorage.class)
public class SimpleRegionStorageMixin {

	@Shadow
	@Final
	private DataFixTypes dataFixType;

	@Inject(
		method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;ILnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;",
		at = @At("HEAD")
	)
	public void frozenLib$captureModdedDataVersions(
		CompoundTag chunkTag, int defaultVersion, CompoundTag dataFixContextTag, int targetVersion, CallbackInfoReturnable<CompoundTag> info,
		@Share("frozenLib$moddedDataVersions") LocalRef<Map<String, Integer>> moddedDataVersionsRef
	) {
		final Map<String, Integer> moddedDataVersions = new HashMap<>();
		QuiltDataFixesInternals.get().forEachFixer(new Dynamic<>(NbtOps.INSTANCE, chunkTag), moddedDataVersions::put);
		moddedDataVersionsRef.set(moddedDataVersions);
	}

	@ModifyReturnValue(
		method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;ILnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;",
		at = @At("RETURN")
	)
	public CompoundTag frozenLib$upgradeChunkTagWithModdedDataVersions(
		CompoundTag original,
		@Share("frozenLib$moddedDataVersions") LocalRef<Map<String, Integer>> moddedDataVersionsRef
	) {
		final Dynamic<Tag> fixed = QuiltDataFixesInternals.get().updateWithAllFixers(
			this.dataFixType.type,
			new Dynamic<>(NbtOps.INSTANCE, original),
			Optional.ofNullable(moddedDataVersionsRef.get())
		);
		return fixed.getValue().asCompound().orElse(original);
	}
}
