/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.impl.PlayerDamageSourceSounds;
import net.frozenblock.lib.sound.api.FrozenClientPacketInbetween;
import net.frozenblock.lib.sound.api.MovingLoopingFadingDistanceSoundEntityManager;
import net.frozenblock.lib.sound.api.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.tags.FrozenItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EntityLoopingSoundInterface, EntityLoopingFadingDistanceSoundInterface {

    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;

	@Unique
    public MovingLoopingSoundEntityManager frozenLib$loopingSoundManager;
	@Unique
    public MovingLoopingFadingDistanceSoundEntityManager frozenLib$loopingFadingDistanceSoundManager;
	@Unique
	public boolean frozenLib$clientFrozenSoundsSynced;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setLoopingSoundManagers(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        this.frozenLib$loopingSoundManager = new MovingLoopingSoundEntityManager(entity);
        this.frozenLib$loopingFadingDistanceSoundManager = new MovingLoopingFadingDistanceSoundEntityManager(entity);
    }

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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addLoopingSoundData(CompoundTag compoundTag, CallbackInfo info) {
        if (this.frozenLib$loopingSoundManager != null) {
            this.frozenLib$loopingSoundManager.save(compoundTag);
        }
        if (this.frozenLib$loopingFadingDistanceSoundManager != null) {
            this.frozenLib$loopingFadingDistanceSoundManager.save(compoundTag);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readLoopingSoundData(CompoundTag compoundTag, CallbackInfo info) {
        this.frozenLib$loopingSoundManager.load(compoundTag);
        this.frozenLib$loopingFadingDistanceSoundManager.load(compoundTag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickSoundsAndIcon(CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!entity.level.isClientSide) {
            this.frozenLib$loopingSoundManager.tick();
            this.frozenLib$loopingFadingDistanceSoundManager.tick();
        } else if (!this.frozenLib$clientFrozenSoundsSynced) {
            FrozenClientPacketInbetween.requestFrozenSoundSync(entity.getId(), entity.level.dimension());
            this.frozenLib$clientFrozenSoundsSynced = true;
        }
    }

	@Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 2))
	public void hurt(Level lvl, Entity par1, byte par2, DamageSource source, float amount) {
		if (par2 == ((byte)2) && par1 instanceof Player) {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeVarInt(par1.getId());
			byteBuf.writeResourceLocation(PlayerDamageSourceSounds.getDamageID(source));
			byteBuf.writeFloat(this.getSoundVolume());
			for (ServerPlayer player : PlayerLookup.tracking((ServerLevel) lvl, par1.blockPosition())) {
				ServerPlayNetworking.send(player, FrozenMain.HURT_SOUND_PACKET, byteBuf);
			}
		}
		lvl.broadcastEntityEvent(par1, par2);
	}

	@Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;"))
	public SoundEvent stopHurtSoundIfTwo(LivingEntity par1, DamageSource par2, byte id) {
		if (id == ((byte)2) && par1 instanceof Player) {
			return null;
		}
		return this.getHurtSound(par2);
	}

	@Shadow
	protected void setLivingEntityFlag(int mask, boolean value) {
		throw new UnsupportedOperationException("Mixin injection failed. - FrozenLib LivingEntityMixin.");
	}

	@Unique
    @Override
    public boolean hasSyncedClient() {
        return this.frozenLib$clientFrozenSoundsSynced;
    }

	@Unique
    @Override
    public MovingLoopingSoundEntityManager getSounds() {
        return this.frozenLib$loopingSoundManager;
    }

	@Unique
    @Override
    public void addSound(ResourceLocation soundID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId) {
        this.frozenLib$loopingSoundManager.addSound(soundID, category, volume, pitch, restrictionId);
    }

	@Unique
    @Override
    public boolean hasSyncedFadingDistanceClient() {
        return this.frozenLib$clientFrozenSoundsSynced;
    }

	@Unique
    @Override
    public MovingLoopingFadingDistanceSoundEntityManager getFadingDistanceSounds() {
        return this.frozenLib$loopingFadingDistanceSoundManager;
    }

	@Unique
    @Override
    public void addFadingDistanceSound(ResourceLocation soundID, ResourceLocation sound2ID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId, float fadeDist, float maxDist) {
        this.frozenLib$loopingFadingDistanceSoundManager.addSound(soundID, sound2ID, category, volume, pitch, restrictionId, fadeDist, maxDist);
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
