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

package net.frozenblock.lib.entity.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SilentTicker extends Marker {
	private int ticks;

	public SilentTicker(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void tick() {
		this.ticks += 1;
		this.tick(this.level, this.getPosition(1F), this.blockPosition(), this.ticks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.ticks = compound.getInt("frozenlib_ticks");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("frozenlib_ticks", this.ticks);
	}

	public abstract void tick(Level level, Vec3 vec3, BlockPos pos, int ticks);

	public Level getLevel() {
		return this.level;
	}

	public int getTicks() {
		return this.ticks;
	}

}
