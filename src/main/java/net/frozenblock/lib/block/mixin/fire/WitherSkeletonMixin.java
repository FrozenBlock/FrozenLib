package net.frozenblock.lib.block.mixin.fire;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkeleton.class)
public class WitherSkeletonMixin {

	@Inject(
		method = "getArrow",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$setFireType(
		ItemStack projectile, float power, ItemStack firingWeapon, CallbackInfoReturnable<AbstractArrow> info,
		@Local(name = "arrow") AbstractArrow arrow
	) {
		final FireData fireData = WitherSkeleton.class.cast(this).getAttached(FireData.ATTACHMENT);
		if (fireData == null || !fireData.type().value().spreadsFromIgniteEnchantments()) return;

		FireData.trySet(arrow, fireData.type());
	}
}
