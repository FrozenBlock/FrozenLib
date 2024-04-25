/*
 * Copyright 2023 The Quilt Project
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.wind.impl;

import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.api.WindManagerExtension;
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
	public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
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
