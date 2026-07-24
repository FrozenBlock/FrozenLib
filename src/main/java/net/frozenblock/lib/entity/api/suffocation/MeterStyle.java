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

package net.frozenblock.lib.entity.api.suffocation;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MeterStyle implements StringRepresentable {
	DRAIN("drain"),
	FILL("fill");

	public static final Codec<MeterStyle> CODEC = StringRepresentable.fromEnum(MeterStyle::values);

	private final String name;

	MeterStyle(String name) {
		this.name = name;
	}

	public int restValue(int capacity) {
		return this == DRAIN ? capacity : 0;
	}

	public int dangerValue(int capacity) {
		return this == DRAIN ? 0 : capacity;
	}

	public float dangerFraction(int units, int capacity) {
		if (capacity <= 0) return 0F;
		final float full = (float) units / (float) capacity;
		return this == DRAIN ? 1F - full : full;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
