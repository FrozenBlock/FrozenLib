/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.sculk.api;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BooleanPropertySculkBehavior(BooleanProperty changingProperty, boolean propertySetValue) implements SculkBehaviour {

	@Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor cursor, LevelAccessor level, @NotNull BlockPos catalystPos, @NotNull RandomSource random, @NotNull SculkSpreader spreadManager, boolean shouldConvertToBlock) {
        BlockState placementState = null;
        BlockPos cursorPos = cursor.getPos();
        BlockState currentState = level.getBlockState(cursorPos);
        if (currentState.hasProperty(this.changingProperty)) {
            if (currentState.getValue(this.changingProperty) != this.propertySetValue) {
                placementState = currentState.setValue(this.changingProperty, this.propertySetValue);
            }
        }

        if (placementState != null) {
            level.setBlock(cursorPos, placementState, 3);
            return cursor.getCharge() - 1;
        }
        return random.nextInt(spreadManager.chargeDecayRate()) == 0 ? Mth.floor((float) cursor.getCharge() * 0.5F) : cursor.getCharge();
    }

    @Override
    public boolean attemptSpreadVein(LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
        BlockState placementState = null;
        BlockState currentState = level.getBlockState(pos);
        if (currentState.hasProperty(this.changingProperty)) {
            if (currentState.getValue(this.changingProperty) != this.propertySetValue) {
                placementState = currentState.setValue(this.changingProperty, this.propertySetValue);
            }
        }

        if (placementState != null) {
            level.setBlock(pos, placementState, 3);
            return true;
        }
        return false;
    }
}
