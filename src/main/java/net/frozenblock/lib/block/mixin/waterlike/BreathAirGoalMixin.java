package net.frozenblock.lib.block.mixin.waterlike;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BreathAirGoal.class)
public class BreathAirGoalMixin {
	//This should be called BreatheAirGoalMixin bruh

	@WrapOperation(
		method = "givesAir",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/block/Blocks;BUBBLE_COLUMN:Lnet/minecraft/world/level/block/Block;",
				opcode = Opcodes.GETSTATIC
			)
		)
	)
	public boolean frozenLib$accountForWaterLikeBubbleColumns(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || WaterLikeBlock.hasBubbleColumn(state);
	}

}
