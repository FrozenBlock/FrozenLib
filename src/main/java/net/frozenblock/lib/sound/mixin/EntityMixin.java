/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.sound.mixin;

import net.frozenblock.lib.sound.api.MovingLoopingFadingDistanceSoundEntityManager;
import net.frozenblock.lib.sound.api.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(
		method = "saveWithoutId",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V",
			shift = At.Shift.AFTER
		)
	)
    public void frozenLib$saveLoopingSoundData(ValueOutput output, CallbackInfo info) {
        if (this.frozenLib$loopingSoundManager != null) this.frozenLib$loopingSoundManager.save(output);
        if (this.frozenLib$loopingFadingDistanceSoundManager != null) this.frozenLib$loopingFadingDistanceSoundManager.save(output);
    }

	@Inject(
		method = "load",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V",
			shift = At.Shift.AFTER
		)
	)
    public void frozenLib$loadLoopingSoundData(ValueInput input, CallbackInfo info) {
        this.frozenLib$loopingSoundManager.load(input);
        this.frozenLib$loopingFadingDistanceSoundManager.load(input);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void frozenLib$tickSounds(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
        if (entity.level().isClientSide()) return;
		this.frozenLib$loopingSoundManager.tick();
		this.frozenLib$loopingFadingDistanceSoundManager.tick();
    }

	@Unique
	@Override
    public MovingLoopingSoundEntityManager frozenLib$getSoundManager() {
        return this.frozenLib$loopingSoundManager;
    }

	@Unique
	@Override
    public void frozenLib$addSound(Identifier soundID, SoundSource source, float volume, float pitch, Identifier restrictionId, boolean stopOnDeath) {
        this.frozenLib$loopingSoundManager.addSound(soundID, source, volume, pitch, restrictionId, stopOnDeath);
    }

	@Unique
	@Override
    public MovingLoopingFadingDistanceSoundEntityManager frozenLib$getFadingSoundManager() {
        return this.frozenLib$loopingFadingDistanceSoundManager;
    }

	@Unique
	@Override
    public void frozenLib$addFadingDistanceSound(
		Identifier closeSound,
		Identifier farSound,
		SoundSource source,
		float volume,
		float pitch,
		Identifier restrictionId,
		boolean stopOnDeath,
		float fadeDist,
		float maxDist
	) {
        this.frozenLib$loopingFadingDistanceSoundManager.addSound(closeSound, farSound, source, volume, pitch, restrictionId, stopOnDeath, fadeDist, maxDist);
    }

}
