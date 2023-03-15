/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind.impl;

import net.frozenblock.lib.wind.api.WindManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class WindStorage extends SavedData {
	public static final String WIND_FILE_ID = "frozenlib_wind";
	private final WindManager windManager;

	public WindStorage(WindManager windManager) {
		this.windManager = windManager;
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
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
		compoundTag.putDouble("cloudX", this.windManager.cloudX);
		compoundTag.putDouble("cloudY", this.windManager.cloudY);
		compoundTag.putDouble("cloudZ", this.windManager.cloudZ);
		compoundTag.putLong("seed", this.windManager.seed);
		return compoundTag;
	}

	public WindStorage load(CompoundTag compoundTag) {
		if (compoundTag.contains("overrideWind")) this.windManager.overrideWind = compoundTag.getBoolean("overrideWind");
		if (compoundTag.contains("commandWindX") && compoundTag.contains("commandWindY") && compoundTag.contains("commandWindZ")) {
			this.windManager.commandWind = new Vec3(compoundTag.getDouble("commandWindX"), compoundTag.getDouble("commandWindY"), compoundTag.getDouble("commandWindZ"));
		}
		if (compoundTag.contains("windX")) this.windManager.windX = compoundTag.getDouble("windX");
		if (compoundTag.contains("windY")) this.windManager.windY = compoundTag.getDouble("windY");
		if (compoundTag.contains("windZ")) this.windManager.windZ = compoundTag.getDouble("windZ");
		if (compoundTag.contains("laggedWindX")) this.windManager.laggedWindX = compoundTag.getDouble("laggedWindX");
		if (compoundTag.contains("laggedWindY")) this.windManager.laggedWindY = compoundTag.getDouble("laggedWindY");
		if (compoundTag.contains("laggedWindZ")) this.windManager.laggedWindZ = compoundTag.getDouble("laggedWindZ");
		if (compoundTag.contains("cloudX")) this.windManager.cloudX = compoundTag.getDouble("cloudX");
		if (compoundTag.contains("cloudY")) this.windManager.cloudY = compoundTag.getDouble("cloudY");
		if (compoundTag.contains("cloudZ")) this.windManager.cloudZ = compoundTag.getDouble("cloudZ");
		if (compoundTag.contains("seed")) this.windManager.setSeed(compoundTag.getLong("seed"));

		return this;
	}
}
