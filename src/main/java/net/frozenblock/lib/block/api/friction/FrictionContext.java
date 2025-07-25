/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.api.friction;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FrictionContext {
	/**
	 * A mutable property that will determine the outputting friction
	 */
	public float friction;

	public final Level level;
	public final LivingEntity entity;
	public final BlockState state;

	public FrictionContext(Level level, LivingEntity entity, BlockState state, float friction) {
		this.level = level;
		this.entity = entity;
		this.state = state;

		this.friction = friction;
	}
}
