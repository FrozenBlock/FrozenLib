package net.frozenblock.lib.block.mixin.entity.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.frozenblock.lib.block.api.entity.BlockEntityWithoutLevelRendererRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

	@WrapOperation(
		method = "renderByItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/block/Blocks;CONDUIT:Lnet/minecraft/world/level/block/Block;"
			)
		)
	)
	public boolean frozenLib$selectBlockEntity(
		BlockState instance, Block block, Operation<Boolean> original,
		@Share("frozenLib$block") LocalRef<Block> customBlock
	) {
		Block usedBlock = instance.getBlock();
		customBlock.set(usedBlock);
		return original.call(instance, block) || BlockEntityWithoutLevelRendererRegistry.hasBlock(usedBlock);
	}

	@WrapOperation(
		method = "renderByItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;renderItem(Lnet/minecraft/world/level/block/entity/BlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)Z",
			ordinal = 0
		)
	)
	public boolean frozenLib$replaceWithNewBlockEntity(
		BlockEntityRenderDispatcher instance,
		BlockEntity blockEntity,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		int packedLight,
		int packedOverlay,
		Operation<Boolean> original,
		@Share("frozenLib$block") LocalRef<Block> customBlock
	) {
		blockEntity = BlockEntityWithoutLevelRendererRegistry.getBlockEntity(customBlock.get()).orElse(blockEntity);
		return original.call(instance, blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
	}

}
