/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.mixin;

import net.frozenblock.lib.sound.api.MovingLoopingFadingDistanceSoundEntityManager;
import net.frozenblock.lib.sound.api.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityLoopingSoundInterface, EntityLoopingFadingDistanceSoundInterface {

	@Unique
    public MovingLoopingSoundEntityManager frozenLib$loopingSoundManager;
	@Unique
    public MovingLoopingFadingDistanceSoundEntityManager frozenLib$loopingFadingDistanceSoundManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void frozenLib$setLoopingSoundManagers(EntityType<? extends Entity> entityType, Level level, CallbackInfo info) {
        Entity entity = Entity.class.cast(this);
        this.frozenLib$loopingSoundManager = new MovingLoopingSoundEntityManager(entity);
        this.frozenLib$loopingFadingDistanceSoundManager = new MovingLoopingFadingDistanceSoundEntityManager(entity);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    public void frozenLib$saveLoopingSoundData(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> info) {
        if (this.frozenLib$loopingSoundManager != null) {
            this.frozenLib$loopingSoundManager.save(compoundTag);
        }
        if (this.frozenLib$loopingFadingDistanceSoundManager != null) {
            this.frozenLib$loopingFadingDistanceSoundManager.save(compoundTag);
        }
    }

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    public void frozenLib$loadLoopingSoundData(CompoundTag compoundTag, CallbackInfo info) {
        this.frozenLib$loopingSoundManager.load(compoundTag);
        this.frozenLib$loopingFadingDistanceSoundManager.load(compoundTag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void frozenLib$tickSounds(CallbackInfo info) {
		Entity entity = Entity.class.cast(this);
        if (!entity.level().isClientSide) {
            this.frozenLib$loopingSoundManager.tick();
            this.frozenLib$loopingFadingDistanceSoundManager.tick();
        }
    }

	@Unique
    @Override
    public MovingLoopingSoundEntityManager getSoundManager() {
        return this.frozenLib$loopingSoundManager;
    }

	@Unique
    @Override
    public void addSound(ResourceLocation soundID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId, boolean stopOnDeath) {
        this.frozenLib$loopingSoundManager.addSound(soundID, category, volume, pitch, restrictionId, stopOnDeath);
    }

	@Unique
    @Override
    public MovingLoopingFadingDistanceSoundEntityManager getFadingSoundManager() {
        return this.frozenLib$loopingFadingDistanceSoundManager;
    }

	@Unique
    @Override
    public void addFadingDistanceSound(ResourceLocation soundID, ResourceLocation sound2ID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId, boolean stopOnDeath, float fadeDist, float maxDist) {
        this.frozenLib$loopingFadingDistanceSoundManager.addSound(soundID, sound2ID, category, volume, pitch, restrictionId, stopOnDeath, fadeDist, maxDist);
    }

}
