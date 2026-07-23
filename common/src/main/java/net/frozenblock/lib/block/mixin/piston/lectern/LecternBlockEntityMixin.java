package net.frozenblock.lib.block.mixin.piston.lectern;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {

	@WrapOperation(
		method = "preRemoveSideEffects",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"
		)
	)
	private Comparable frozenLib$fixLecternCrashOnMovedByPiston(BlockState instance, Property property, Operation<Comparable> original) {
		if (property == LecternBlock.HAS_BOOK) return instance.getValueOrElse(LecternBlock.HAS_BOOK, false);
		return original.call(instance, property);
	}
}
