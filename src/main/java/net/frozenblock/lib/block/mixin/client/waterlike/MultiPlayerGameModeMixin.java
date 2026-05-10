package net.frozenblock.lib.block.mixin.client.waterlike;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
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
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	public boolean frozenLib$destroyWaterLikeBlock(
		Level instance, BlockPos pos, BlockState blockState, int updateFlags, Operation<Boolean> original,
		@Share("frozenLib$destroyedState") LocalRef<BlockState> destroyedStateRef
	) {
		if (destroyedStateRef.get().getBlock() instanceof WaterLikeBlock) {
			instance.setBlock(pos, Blocks.AIR.defaultBlockState(), updateFlags);
			return true;
		}
		return original.call(instance, pos, blockState, updateFlags);
	}
}
