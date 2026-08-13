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

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.worldupdate.RegionStorageUpgrader;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RegionStorageUpgrader.class)
public class RegionStorageUpgraderMixin {

	@ModifyVariable(method = "tryProcessOnePosition", at = @At("STORE"), name = "changed")
	public boolean frozenLib$markAsChanged(
		boolean changed,
		@Local(name = "chunkTag") CompoundTag chunkTag
	) {
		final Map<String, Integer> moddedDataVersions = new HashMap<>();
		QuiltDataFixesInternals.get().forEachFixer(new Dynamic<>(NbtOps.INSTANCE, chunkTag), moddedDataVersions::put);

		final Map<String, Integer> latestModdedDataVersions = new HashMap<>();
		QuiltDataFixesInternals.get().forEachFixer(latestModdedDataVersions::put);

		if (!moddedDataVersions.equals(latestModdedDataVersions)) return true;
		return changed;
	}
}
