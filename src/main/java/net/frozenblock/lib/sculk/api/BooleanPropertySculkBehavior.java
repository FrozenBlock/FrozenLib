/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
