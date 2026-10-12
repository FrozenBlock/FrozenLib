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

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class WindDisturbance<S, T extends WindDisturbanceType<S>> {
	private final S source;
	private final T type;
	private Vec3 origin;
	private double scale;
	private AABB area;

	public WindDisturbance(Level level, S source, T type) {
		this.source = source;
		this.type = type;
		this.tick(level);
	}

	public void tick(Level level) {
		this.origin = this.type.origin(this.source, level);
		this.scale = this.type.scale(this.source, level);
		this.area = this.type.area(this.source, level, this.origin, this.scale);
	}

	public AABB area() {
		return this.area;
	}

	public boolean withinArea(Vec3 pos) {
		return this.area.contains(pos);
	}

	public Vec3 origin() {
		return this.origin;
	}

	public boolean expired(Level level) {
		return this.type.expired(this.source, level);
	}

	public WindDisturbanceResult sample(Level level, Vec3 target) {
		if (!this.area.contains(target)) return WindDisturbanceResult.PASS;
		return this.type.sample(this.source, level, this.origin, this.area, target, this.scale);
	}

	public T type() {
		return this.type;
	}
}
