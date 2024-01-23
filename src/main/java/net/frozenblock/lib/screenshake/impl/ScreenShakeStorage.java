/*
 * Copyright 2023-2024 FrozenBlock
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

import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class ScreenShakeStorage extends SavedData {
	public static final String SCREEN_SHAKE_FILE_ID = "frozenlib_screen_shakes";
	private final ScreenShakeManager screenShakeManager;

	public ScreenShakeStorage(ScreenShakeManager screenShakeManager) {
		this.screenShakeManager = screenShakeManager;
		this.setDirty();
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		this.screenShakeManager.save(compoundTag);
		return compoundTag;
	}

	public ScreenShakeStorage load(CompoundTag compoundTag) {
		this.screenShakeManager.load(compoundTag);
		return this;
	}
}
