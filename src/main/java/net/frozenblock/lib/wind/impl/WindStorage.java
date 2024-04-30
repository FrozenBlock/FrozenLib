/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.wind.impl;

import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.api.WindManagerExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindStorage extends SavedData {
	public static final String WIND_FILE_ID = "frozenlib_wind";
	private final WindManager windManager;

	public WindStorage(WindManager windManager) {
		this.windManager = windManager;
		this.setDirty();
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.Provider provider) {
		compoundTag.putLong("time", this.windManager.time);
		compoundTag.putBoolean("overrideWind", this.windManager.overrideWind);
		compoundTag.putDouble("commandWindX", this.windManager.commandWind.x());
		compoundTag.putDouble("commandWindY", this.windManager.commandWind.y());
		compoundTag.putDouble("commandWindZ", this.windManager.commandWind.z());
		compoundTag.putDouble("windX", this.windManager.windX);
		compoundTag.putDouble("windY", this.windManager.windY);
		compoundTag.putDouble("windZ", this.windManager.windZ);
		compoundTag.putDouble("laggedWindX", this.windManager.laggedWindX);
		compoundTag.putDouble("laggedWindY", this.windManager.laggedWindY);
		compoundTag.putDouble("laggedWindZ", this.windManager.laggedWindZ);
		compoundTag.putLong("seed", this.windManager.seed);

		// EXTENSIONS
		for (WindManagerExtension extension : this.windManager.attachedExtensions) {
			CompoundTag extensionTag = new CompoundTag();
			extension.save(extensionTag);
			compoundTag.put(extension.extensionID().toString(), extensionTag);
		}

		FrozenLogUtils.log("Saving WindManager data.", FrozenSharedConstants.UNSTABLE_LOGGING);

		return compoundTag;
	}

	public static @NotNull WindStorage load(@NotNull CompoundTag compoundTag, WindManager manager) {
		WindStorage windStorage = new WindStorage(manager);

		windStorage.windManager.time = compoundTag.getLong("time");
		windStorage.windManager.overrideWind = compoundTag.getBoolean("overrideWind");
		windStorage.windManager.commandWind = new Vec3(compoundTag.getDouble("commandWindX"), compoundTag.getDouble("commandWindY"), compoundTag.getDouble("commandWindZ"));
		windStorage.windManager.windX = compoundTag.getDouble("windX");
		windStorage.windManager.windY = compoundTag.getDouble("windY");
		windStorage.windManager.windZ = compoundTag.getDouble("windZ");
		windStorage.windManager.laggedWindX = compoundTag.getDouble("laggedWindX");
		windStorage.windManager.laggedWindY = compoundTag.getDouble("laggedWindY");
		windStorage.windManager.laggedWindZ = compoundTag.getDouble("laggedWindZ");
		windStorage.windManager.setSeed(compoundTag.getLong("seed"));

		// EXTENSIONS
		for (WindManagerExtension extension : windStorage.windManager.attachedExtensions) {
			CompoundTag extensionTag = compoundTag.getCompound(extension.extensionID().toString());
			extension.load(extensionTag);
		}

		FrozenLogUtils.log("Loading WindManager data.", FrozenSharedConstants.UNSTABLE_LOGGING);

		return windStorage;
	}
}
