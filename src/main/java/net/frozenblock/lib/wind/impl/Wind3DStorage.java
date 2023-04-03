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

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.wind.api.wind3d.WindManager3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class Wind3DStorage extends SavedData {
	public static final String WIND_FILE_ID = "frozenlib_wind_3d";
	private final WindManager3D windManager3D;

	public Wind3DStorage(WindManager3D windManager3D) {
		this.windManager3D = windManager3D;
		this.setDirty();
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		compoundTag.putLong("time", this.windManager3D.time);
		compoundTag.putLong("seed", this.windManager3D.seed);

		FrozenMain.log("Saving WindManager3D data.", FrozenMain.UNSTABLE_LOGGING);

		return compoundTag;
	}

	public Wind3DStorage load(CompoundTag compoundTag) {
		this.windManager3D.time = compoundTag.getLong("time");
		this.windManager3D.setSeed(compoundTag.getLong("seed"));

		FrozenMain.log("Loading WindManager3D data.", FrozenMain.UNSTABLE_LOGGING);

		return this;
	}
}
