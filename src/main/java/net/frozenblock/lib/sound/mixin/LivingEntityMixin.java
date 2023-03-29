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

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.sound.api.damagesource.PlayerDamageSourceSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, float f, boolean bl, float g, boolean bl2, Entity entity2, byte event) {
        var entity = LivingEntity.class.cast(this);
        if (entity instanceof Player && event == EntityEvent.HURT && PlayerDamageSourceSounds.containsSource(source)) {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            byteBuf.writeVarInt(entity.getId());
            byteBuf.writeResourceLocation(PlayerDamageSourceSounds.getDamageID(source));
            byteBuf.writeFloat(this.getSoundVolume());
            for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) entity.level, entity.blockPosition())) {
                ServerPlayNetworking.send(player, FrozenMain.HURT_SOUND_PACKET, byteBuf);
            }
        }
    }

    @Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;"))
	public SoundEvent stopHurtSoundIfTwo(LivingEntity par1, DamageSource par2, byte id) {
		if (par1 instanceof Player && id == EntityEvent.HURT && PlayerDamageSourceSounds.containsSource(par2)) {
			return null;
		}
		return this.getHurtSound(par2);
	}

	@Shadow
	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		throw new RuntimeException("Mixin injection failed - FrozenLib LivingEntityMixin");
	}

	@Shadow
	protected float getSoundVolume() {
		throw new RuntimeException("Mixin injection failed - FrozenLib LivingEntityMixin");
	}
}
