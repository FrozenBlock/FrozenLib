package net.frozenblock.lib.levelgen.feature.mixin.patch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.levelgen.feature.api.feature.patch.FlexibleVegetationPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VegetationPatchFeature.class)
public class NoBaseBlockMixin {

	@WrapOperation(
		method = "placeGround",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	public boolean frozenLib$skipBaseBlock(WorldGenLevel instance, BlockPos pos, BlockState blockState, int i, Operation<Boolean> original) {
		if (VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible && !flexible.flexibleComponents.placeBaseBlock()) return true;
		return original.call(instance, pos, blockState, i);
	}
}
