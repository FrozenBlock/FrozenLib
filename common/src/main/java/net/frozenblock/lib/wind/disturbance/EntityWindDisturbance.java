/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.wind.disturbance;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class EntityWindDisturbance<T extends Entity> implements WindDisturbance<T> {

	@Override
	public double scale(T source, Level level, Vec3 target) {
		return source instanceof LivingEntity livingEntity
			? livingEntity.getScale() * livingEntity.getAgeScale()
			: WindDisturbance.super.scale(source, level, target);
	}

	@Override
	public Vec3 origin(T source, Level level) {
		return source.getBoundingBox().getCenter();
	}

	@Override
	public boolean expired(T source, Level level) {
		return source.isRemoved();
	}
}
