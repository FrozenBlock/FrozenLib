package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.replacements_and_lists.HopperUntouchableList;
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
