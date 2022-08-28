package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.replacements_and_lists.BonemealBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState state = world.getBlockState(blockPos);
        Direction direction = context.getClickedFace();
        Direction horizontal = context.getHorizontalDirection();
        if (BonemealBehaviors.bonemeals.containsKey(state.getBlock())) {
            if (BonemealBehaviors.bonemeals.get(state.getBlock()).bonemeal(context, world, blockPos, state, direction, horizontal)) {
                context.getItemInHand().shrink(1);
                info.setReturnValue(InteractionResult.sidedSuccess(world.isClientSide));
                info.cancel();
            }
        }
    }

}
