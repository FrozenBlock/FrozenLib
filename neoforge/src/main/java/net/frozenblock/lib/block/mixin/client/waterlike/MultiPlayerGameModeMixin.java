package net.frozenblock.lib.block.mixin.client.waterlike;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

	@ModifyExpressionValue(
		method = "destroyBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	public BlockState frozenLib$captureDestroyedBlockState(
		BlockState original,
		@Share("frozenLib$destroyedState") LocalRef<BlockState> destroyedStateRef
	) {
		destroyedStateRef.set(original);
		return original;
	}

	@WrapOperation(
		method = "destroyBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;onDestroyedByPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/world/level/material/FluidState;)Z"
		)
	)
	public boolean frozenLib$destroyWaterLikeBlock(
		BlockState instance, Level level, BlockPos blockPos, Player player,ItemStack itemStack,
		boolean b, FluidState fluidState, Operation<Boolean> original,
		@Share("frozenLib$destroyedState") LocalRef<BlockState> destroyedStateRef
	) {
		if (destroyedStateRef.get().getBlock() instanceof WaterLikeBlock) {
			level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
			return true;
		}
		return original.call(instance, level, blockPos, player, itemStack, b, fluidState);
	}
}
