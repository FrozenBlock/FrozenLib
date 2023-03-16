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

import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class ScreenShakeManager {
	public static final ArrayList<ScreenShake> SCREEN_SHAKES = new ArrayList<>();
	private final ServerLevel level;

	public ScreenShakeManager(ServerLevel level) {
		this.level = level;
	}

	public static void tick() {
		SCREEN_SHAKES.removeIf(ScreenShake::shouldRemove);
		for (ScreenShake shake : SCREEN_SHAKES) {
			shake.ticks += 1;
		}
	}

	public static void addShake(float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		SCREEN_SHAKES.add(new ScreenShake(intensity, duration, falloffStart, pos, maxDistance, ticks));
	}

	public static class ScreenShake {
		private final float intensity;
		public final int duration;
		private final int durationFalloffStart;
		protected Vec3 pos;
		public final float maxDistance;
		public int ticks;

		public ScreenShake(float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance, int ticks) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.pos = pos;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
		}

		public boolean shouldRemove() {
			return this.ticks > this.duration;
		}
	}



	public static ScreenShakeManager getScreenShakeManager(ServerLevel level) {
		return ((ScreenShakeManagerInterface)level).frozenLib$getScreenShakeManager();
	}

	public ScreenShakeStorage createData() {
		return new ScreenShakeStorage(this);
	}

	public ScreenShakeStorage createData(CompoundTag nbt) {
		return this.createData().load(nbt);
	}

}
