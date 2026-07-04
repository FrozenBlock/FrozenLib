package net.frozenblock.lib.item.mixin.shovel;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.item.api.shovel.ShovelApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

	@WrapOperation(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getToolModifiedState(Lnet/minecraft/world/item/context/UseOnContext;Lnet/neoforged/neoforge/common/ItemAbility;Z)Lnet/minecraft/world/level/block/state/BlockState;",
			ordinal = 0
		)
	)
	public BlockState frozenlib$runShovelBehavior(
		BlockState instance, UseOnContext context, ItemAbility itemAbility, boolean simulate, Operation<BlockState> original
	) {
		final Level level = context.getLevel();
		final BlockPos pos = context.getClickedPos();
		final Direction direction = context.getClickedFace();
		final ShovelApi.ShovelBehavior shovelBehavior = ShovelApi.get(instance.getBlock());

		if (shovelBehavior != null && shovelBehavior.meetsRequirements(level, pos, direction, instance)) {
			final BlockState outputState = shovelBehavior.getOutputBlockState(instance);
			if (outputState != null) {
				shovelBehavior.onSuccess(level, pos, direction, outputState, instance);
				return outputState;
			}
		}

		return original.call(instance, context, itemAbility, simulate);
	}

}
