package net.frozenblock.lib.block.mixin.waterlike;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

	@Shadow
	@Final
	protected ServerPlayer player;

	@WrapOperation(
		method = "destroyBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"
		)
	)
	public boolean frozenLib$destroyWaterLikeBlock(
		ServerLevel instance, BlockPos pos, boolean movedByPiston, Operation<Boolean> original,
		@Local(name = "adjustedState") BlockState adjustedState
	) {
		if (adjustedState.getBlock() instanceof WaterLikeBlock) {
			instance.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return true;
		}
		return original.call(instance, pos, movedByPiston);
	}
}
