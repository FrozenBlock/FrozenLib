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

package net.frozenblock.lib.block.impl.waterlike;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

public enum BubbleColumnDirection implements StringRepresentable {
	NONE("none", Optional.empty()),
	UP("up", Optional.of(Direction.UP)),
	DOWN("down", Optional.of(Direction.DOWN));
	private final Optional<Direction> direction;
	private final String name;

	BubbleColumnDirection(String name, Optional<Direction> direction) {
		this.name = name;
		this.direction = direction;
	}

	public Optional<Direction> direction() {
		return direction;
	}

	public static BubbleColumnDirection getFromBubbleColumn(BlockState bubbleColumnState) {
		return bubbleColumnState.getValue(BubbleColumnBlock.DRAG_DOWN) ? DOWN : UP;
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
