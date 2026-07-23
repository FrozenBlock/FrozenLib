/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.block.impl.piston;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface PistonMovingBlockEntityInterface {
	default void frozenLib$setPushedBlockEntityTag(@Nullable CompoundTag tag) {
		throw new AssertionError();
	}

	@Nullable
	default CompoundTag frozenLib$getPushedBlockEntityTag() {
		throw new AssertionError();
	}

	@Nullable
	default BlockEntity frozenLib$getPushedFakeBlockEntity() {
		throw new AssertionError();
	}
}
