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

package net.frozenblock.lib.storage.mixin;

import net.frozenblock.lib.storage.api.HopperUntouchableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @Inject(at = @At("HEAD"), method = "ejectItems", cancellable = true)
    private static void preventEjection(Level world, BlockPos pos, BlockState state, Container inventory, CallbackInfoReturnable<Boolean> info) {
        if (HopperUntouchableList.inventoryContainsBlacklisted(getAttachedContainer(world, pos, state))) {
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "suckInItems", cancellable = true)
    private static void preventInsertion(Level world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (HopperUntouchableList.inventoryContainsBlacklisted(getSourceContainer(world, hopper))) {
            info.setReturnValue(false);
        }
    }

    @Nullable
    @Shadow
    private static Container getAttachedContainer(Level world, BlockPos pos, BlockState state) {
        throw new AssertionError("Mixin injection failed. - FrozenLib HopperBlockEntityMixin");
    }

    @Nullable
    @Shadow
    private static Container getSourceContainer(Level world, Hopper hopper) {
        throw new AssertionError("Mixin injection failed. - FrozenLib HopperBlockEntityMixin");
    }
}
