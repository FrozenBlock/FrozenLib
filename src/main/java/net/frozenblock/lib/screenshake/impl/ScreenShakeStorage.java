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

package net.frozenblock.lib.screenshake.impl;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class ScreenShakeStorage extends SavedData {
	public static final String WIND_FILE_ID = "frozenlib_screen_shakes";
	private final ScreenShakeManager screenShakeManager;

	public ScreenShakeStorage(ScreenShakeManager screenShakeManager) {
		this.screenShakeManager = screenShakeManager;
		this.setDirty();
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
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
		compoundTag.putDouble("cloudX", this.windManager.cloudX);
		compoundTag.putDouble("cloudY", this.windManager.cloudY);
		compoundTag.putDouble("cloudZ", this.windManager.cloudZ);
		compoundTag.putLong("seed", this.windManager.seed);

		FrozenMain.log("Saving WindManager data.", FrozenMain.UNSTABLE_LOGGING);

		return compoundTag;
	}

	public ScreenShakeStorage load(CompoundTag compoundTag) {
		this.windManager.time = compoundTag.getLong("time");
		this.windManager.overrideWind = compoundTag.getBoolean("overrideWind");
		this.windManager.commandWind = new Vec3(compoundTag.getDouble("commandWindX"), compoundTag.getDouble("commandWindY"), compoundTag.getDouble("commandWindZ"));
		this.windManager.windX = compoundTag.getDouble("windX");
		this.windManager.windY = compoundTag.getDouble("windY");
		this.windManager.windZ = compoundTag.getDouble("windZ");
		this.windManager.laggedWindX = compoundTag.getDouble("laggedWindX");
		this.windManager.laggedWindY = compoundTag.getDouble("laggedWindY");
		this.windManager.laggedWindZ = compoundTag.getDouble("laggedWindZ");
		this.windManager.cloudX = compoundTag.getDouble("cloudX");
		this.windManager.cloudY = compoundTag.getDouble("cloudY");
		this.windManager.cloudZ = compoundTag.getDouble("cloudZ");
		this.windManager.setSeed(compoundTag.getLong("seed"));

		FrozenMain.log("Loading WindManager data.", FrozenMain.UNSTABLE_LOGGING);

		return this;
	}
}
