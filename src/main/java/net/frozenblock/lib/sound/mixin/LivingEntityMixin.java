/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.mixin;

import net.frozenblock.lib.sound.api.damagesource.PlayerDamageSourceSounds;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow
	protected ItemStack useItem;
	@Shadow
	protected int useItemRemaining;

	@Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
	private void preventStartingGameEvent(InteractionHand hand, CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		ItemStack stack = entity.getItemInHand(hand);
		if (!stack.isEmpty() && !entity.isUsingItem()) {
			if (stack.is(FrozenItemTags.NO_USE_GAME_EVENTS)) {
				info.cancel();
				this.useItem = stack;
				this.useItemRemaining = stack.getUseDuration();
				if (!entity.level.isClientSide) {
					this.setLivingEntityFlag(1, true);
					this.setLivingEntityFlag(2, hand == InteractionHand.OFF_HAND);
				}
			}
		}
	}

	@Inject(method = "stopUsingItem", at = @At("HEAD"), cancellable = true)
	public void preventStoppingGameEvent(CallbackInfo info) {
		LivingEntity entity = LivingEntity.class.cast(this);
		if (!entity.level.isClientSide) {
			ItemStack stack = entity.getUseItem();
			if (stack.is(FrozenItemTags.NO_USE_GAME_EVENTS)) {
				this.setLivingEntityFlag(1, false);
				info.cancel();
			}
		}
	}

	@ModifyVariable(method = "playHurtSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
	private SoundEvent playHurtSound(SoundEvent original, DamageSource source) {
		if (PlayerDamageSourceSounds.containsSource(source)) {
			return PlayerDamageSourceSounds.getDamageSound(source);
		}
		return original;
	}

	@ModifyVariable(method = "handleDamageEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
	private SoundEvent handleDamageEvent(SoundEvent original, DamageSource source) {
		if (PlayerDamageSourceSounds.containsSource(source)) {
			return PlayerDamageSourceSounds.getDamageSound(source);
		}
		return original;
	}

	@Shadow
	protected void setLivingEntityFlag(int mask, boolean value) {
		throw new UnsupportedOperationException("Mixin injection failed. - FrozenLib LivingEntityMixin.");
	}

}
