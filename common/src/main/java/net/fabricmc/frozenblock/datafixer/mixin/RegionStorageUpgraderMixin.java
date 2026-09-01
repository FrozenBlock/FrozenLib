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

package net.fabricmc.frozenblock.datafixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.worldupdate.RegionStorageUpgrader;
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
		// Collect DataVersions currently in chunkTag
		final Map<String, Integer> moddedDataVersions = new HashMap<>();
		FabricDataFixesInternals.get().forEachFixer(new Dynamic<>(NbtOps.INSTANCE, chunkTag), moddedDataVersions::put);

		// Collect all up-to-date DataVersions
		final Map<String, Integer> latestModdedDataVersions = new HashMap<>();
		FabricDataFixesInternals.get().forEachFixer(latestModdedDataVersions::put);

		// If stored DataVersions differ from the up-to-date ones, mark as changed
		if (!moddedDataVersions.equals(latestModdedDataVersions)) return true;
		return changed;
	}
}
