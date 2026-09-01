/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.mixin.fire;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.frozenblock.lib.block.api.fire.FireEvents;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.resources.ResourceKey;
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
		final FireData fireData = FireData.ATTACHMENT.get(WitherSkeleton.class.cast(this));
		if (fireData == null || !fireData.type().value().spreadSettings().spreadsFromIgniteEnchantments()) return;

		final ResourceKey<FireType> fireType = FireEvents.SELECT_FIRE_TYPE.invoker().selectFireType(
			arrow,
			Optional.empty(),
			Optional.of(WitherSkeleton.class.cast(this)),
			Optional.empty(),
			true
		);
		FireData.trySet(arrow, fireType);
	}
}
