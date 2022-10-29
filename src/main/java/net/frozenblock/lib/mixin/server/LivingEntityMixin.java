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

import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.sound.FrozenClientPacketInbetween;
import net.frozenblock.lib.sound.MovingLoopingFadingDistanceSoundEntityManager;
import net.frozenblock.lib.sound.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.spotting_icons.SpottingIconManager;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.tags.FrozenItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EntityLoopingSoundInterface, EntityLoopingFadingDistanceSoundInterface, EntitySpottingIconInterface {

    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;

	@Unique
    public MovingLoopingSoundEntityManager frozenLib$loopingSoundManager;
	@Unique
    public MovingLoopingFadingDistanceSoundEntityManager frozenLib$loopingFadingDistanceSoundManager;
	@Unique
	public SpottingIconManager frozenLib$SpottingIconManager;
	@Unique
	public boolean frozenLib$clientFrozenSoundAndIconsSynced;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setLoopingSoundManager(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        this.frozenLib$loopingSoundManager = new MovingLoopingSoundEntityManager(entity);
        this.frozenLib$loopingFadingDistanceSoundManager = new MovingLoopingFadingDistanceSoundEntityManager(entity);
		this.frozenLib$SpottingIconManager = new SpottingIconManager(entity);
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
		if (this.frozenLib$SpottingIconManager != null) {
			this.frozenLib$SpottingIconManager.save(compoundTag);
		}
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readLoopingSoundData(CompoundTag compoundTag, CallbackInfo info) {
        this.frozenLib$loopingSoundManager.load(compoundTag);
        this.frozenLib$loopingFadingDistanceSoundManager.load(compoundTag);
		this.frozenLib$SpottingIconManager.load(compoundTag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickSoundsAndIcon(CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!entity.level.isClientSide) {
            this.frozenLib$loopingSoundManager.tick();
            this.frozenLib$loopingFadingDistanceSoundManager.tick();
			this.frozenLib$SpottingIconManager.tick();
        } else if (!this.frozenLib$clientFrozenSoundAndIconsSynced) {
            FrozenClientPacketInbetween.requestFrozenSoundSync(entity.getId(), entity.level.dimension());
			FrozenClientPacketInbetween.requestFrozenIconSync(entity.getId(), entity.level.dimension());
            this.frozenLib$clientFrozenSoundAndIconsSynced = true;
        }
    }

	@Shadow
	protected void setLivingEntityFlag(int mask, boolean value) {
		throw new UnsupportedOperationException("Mixin injection failed. - FrozenLib LivingEntityMixin.");
	}

	@Unique
    @Override
    public boolean hasSyncedClient() {
        return this.frozenLib$clientFrozenSoundAndIconsSynced;
    }

	@Override
	public SpottingIconManager getSpottingIconManager() {
		return this.frozenLib$SpottingIconManager;
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
        return this.frozenLib$clientFrozenSoundAndIconsSynced;
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
}
