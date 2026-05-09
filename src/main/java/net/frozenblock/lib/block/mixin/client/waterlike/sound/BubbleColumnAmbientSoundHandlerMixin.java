package net.frozenblock.lib.block.mixin.client.waterlike.sound;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(BubbleColumnAmbientSoundHandler.class)
public class BubbleColumnAmbientSoundHandlerMixin {

	@WrapOperation(
		method = "lambda$tick$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		require = 0
	)
	private static boolean frozenLib$filterWithWaterLike(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || WaterLikeBlock.hasBubbleColumn(state);
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		require = 0
	)
	private boolean frozenLib$checkIfBubbleColumnOrWaterLikeAsBubbleColumn(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || WaterLikeBlock.hasBubbleColumn(state);
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;",
			ordinal = 0
		),
		require = 0
	)
	private Comparable<Boolean> frozenLib$checkIfBubbleColumnOrWaterLikeAsBubbleColumnDraggingDown(BlockState state, Property<?> property, Operation<Comparable<Boolean>> operation) {
		return state.hasProperty(BubbleColumnBlock.DRAG_DOWN) ? operation.call(state, property) : WaterLikeBlock.isDraggingDownAsBubbleColumn(state);
	}

}
