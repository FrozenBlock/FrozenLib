/*
 * Copyright 2022-2023 FrozenBlock
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

package net.frozenblock.lib.storage.mixin;

import net.frozenblock.lib.impl.HopperUntouchableList;
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
            info.cancel();
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "suckInItems", cancellable = true)
    private static void preventInsertion(Level world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (HopperUntouchableList.inventoryContainsBlacklisted(getSourceContainer(world, hopper))) {
            info.cancel();
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
