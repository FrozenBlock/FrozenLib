/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.block.api.sculk;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link SculkBehaviour} that sets a {@link Property} to a defined value.
 *
 * @param property The {@link Property} to change.
 * @param value The value to set the {@link Property} to.
 */
public record BlockStatePropertySculkBehavior<T extends Comparable<T>>(Property<T> property, T value) implements SculkBehaviour {

	@Override
	public int attemptUseCharge(
		SculkSpreader.ChargeCursor cursor,
		LevelAccessor level,
		BlockPos catalystPos,
		RandomSource random,
		SculkSpreader spreadManager,
		boolean shouldConvertToBlock
	) {
		BlockState placementState = null;
		final BlockPos cursorPos = cursor.getPos();
		final BlockState currentState = level.getBlockState(cursorPos);
		if (currentState.getValueOrElse(this.property, this.value) != this.value) placementState = currentState.setValue(this.property, this.value);

		if (placementState == null) return random.nextInt(spreadManager.chargeDecayRate()) == 0 ? Mth.floor((float) cursor.getCharge() * 0.5F) : cursor.getCharge();
		level.setBlock(cursorPos, placementState, Block.UPDATE_ALL);
		return cursor.getCharge() - 1;
	}

	@Override
	public boolean attemptSpreadVein(
		LevelAccessor level,
		BlockPos pos,
		BlockState state,
		@Nullable Collection<Direction> directions,
		boolean markForPostProcessing
	) {
		BlockState placementState = null;
		final BlockState currentState = level.getBlockState(pos);
		if (currentState.getValueOrElse(this.property, this.value) != this.value) placementState = currentState.setValue(this.property, this.value);

		if (placementState == null) return false;
		level.setBlock(pos, placementState, Block.UPDATE_ALL);
		return true;
	}
}
