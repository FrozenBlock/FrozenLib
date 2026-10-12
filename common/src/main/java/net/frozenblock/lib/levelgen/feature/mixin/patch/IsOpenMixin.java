package net.frozenblock.lib.levelgen.feature.mixin.patch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import net.frozenblock.lib.levelgen.feature.api.feature.patch.FlexibleVegetationPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VegetationPatchFeature.class)
public class IsOpenMixin {

	@WrapOperation(
		method = "placeGroundPatch",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;isStateAtPosition(Lnet/minecraft/core/BlockPos;Ljava/util/function/Predicate;)Z",
			ordinal = 0
		)
	)
	public boolean frozenLib$moveInwardsIfOpen(WorldGenLevel instance, BlockPos pos, Predicate predicate, Operation<Boolean> original) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| flexible.flexibleComponents.isOpen().isEmpty()
		) return original.call(instance, pos, predicate);

		return flexible.flexibleComponents.isOpen().orElseThrow().test(instance, pos);
	}

	@WrapOperation(
		method = "placeGroundPatch",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;isStateAtPosition(Lnet/minecraft/core/BlockPos;Ljava/util/function/Predicate;)Z",
			ordinal = 1
		)
	)
	public boolean frozenLib$moveOutwardsIfNotOpen(WorldGenLevel instance, BlockPos pos, Predicate predicate, Operation<Boolean> original) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| flexible.flexibleComponents.isOpen().isEmpty()
		) return original.call(instance, pos, predicate);

		return !flexible.flexibleComponents.isOpen().orElseThrow().test(instance, pos);
	}

	@WrapOperation(
		method = "placeGroundPatch",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/WorldGenLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	public boolean frozenLib$placeGroundIfOpen(WorldGenLevel instance, BlockPos pos, Operation<Boolean> original) {
		if (!(VegetationPatchFeature.class.cast(this) instanceof FlexibleVegetationPatchFeature flexible)
			|| flexible.flexibleComponents.isOpen().isEmpty()
		) return original.call(instance, pos);

		return flexible.flexibleComponents.isOpen().orElseThrow().test(instance, pos);
	}
}
