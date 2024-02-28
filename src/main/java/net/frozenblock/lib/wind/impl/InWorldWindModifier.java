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

package net.frozenblock.lib.wind.impl;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class InWorldWindModifier {
	public static final Pair<Pair<Double, Double>, Vec3> DUMMY_MODIFIER = Pair.of(Pair.of(0D, 0D), Vec3.ZERO);
	public static final InWorldWindModifier DUMMY_IN_WORLD_WIND_MODIFIER = new InWorldWindModifier(
		Vec3.ZERO,
		AABB.ofSize(new Vec3(0D, -999999D, 0D), 1D, 1D, 1D),
		(level, windOrigin, affectedArea, windTarget) -> DUMMY_MODIFIER
	);

	private final Vec3 origin;
	private final AABB affectedArea;
	private final Modifier modifier;

    public InWorldWindModifier(Vec3 origin, AABB affectedArea, Modifier modifier) {
        this.origin = origin;
        this.affectedArea = affectedArea;
        this.modifier = modifier;
    }

	public Pair<Pair<Double, Double>, Vec3> calculateWindAndWeight(Level level, Vec3 windTarget) {
		if (this.affectedArea.contains(windTarget)) {
			return this.modifier.calculateWindAndWeight(level, this.origin, this.affectedArea, windTarget);
		}
		return DUMMY_MODIFIER;
	}

    @FunctionalInterface
	public interface Modifier {
		Pair<Pair<Double, Double>, Vec3> calculateWindAndWeight(Level level, Vec3 windOrigin, AABB affectedArea, Vec3 windTarget);
	}
}
