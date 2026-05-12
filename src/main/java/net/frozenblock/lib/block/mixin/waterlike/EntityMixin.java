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

package net.frozenblock.lib.block.mixin.waterlike;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.frozenblock.lib.block.api.waterlike.WaterLikeTypes;
import net.frozenblock.lib.block.impl.waterlike.InWaterLikeInterface;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements InWaterLikeInterface {

	@Shadow
	public abstract Level level();
	@Shadow
	protected boolean wasTouchingWater;
	@Shadow
	public abstract boolean isUnderWater();
	@Shadow
	public abstract RandomSource getRandom();

	@Unique
	private ParticleOptions frozenLib$replacementBubbleParticle;
	@Unique
	private ParticleOptions frozenLib$replacementSplashParticle;
	@Unique
	private final List<WaterLikeType> frozenLib$waterLikesInside = new ArrayList<>();
	@Unique
	private final List<WaterLikeType> frozenLib$waterLikesTouching = new ArrayList<>();

	@WrapOperation(
		method = "getBlockSpeedFactor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/block/Blocks;BUBBLE_COLUMN:Lnet/minecraft/world/level/block/Block;",
				opcode = Opcodes.GETSTATIC
			)
		)
	)
	public boolean frozenLib$accountForWaterLikeBubbleColumns(BlockState state, Object block, Operation<Boolean> operation) {
		return operation.call(state, block) || WaterLikeBlock.hasBubbleColumn(state);
	}

	@Unique
	@Override
	public void frozenLib$addInWaterLike(WaterLikeType type) {
		this.frozenLib$waterLikesInside.add(type);
	}

	@Unique
	@Override
	public void frozenLib$clearInWaterLikes() {
		this.frozenLib$waterLikesInside.clear();
	}

	@Unique
	@Override
	public boolean frozenLib$wasInWaterLike(WaterLikeType type) {
		return this.frozenLib$waterLikesInside.contains(type);
	}

	@Unique
	@Override
	public List<WaterLikeType> frozenLib$waterLikesInside() {
		return this.frozenLib$waterLikesInside;
	}

	@Inject(method = "updateFluidInteraction", at = @At("TAIL"))
	public void frozenLib$clearTouchingWaterLikeStatuses(CallbackInfoReturnable<Boolean> info) {
		if (!this.wasTouchingWater) this.frozenLib$clearTouchingWaterLikes();
	}

	@Unique
	@Override
	public void frozenLib$addTouchingWaterLike(WaterLikeType type) {
		this.frozenLib$waterLikesTouching.add(type);
	}

	@Unique
	@Override
	public void frozenLib$clearTouchingWaterLikes() {
		this.frozenLib$waterLikesTouching.clear();
	}

	@Unique
	@Override
	public boolean frozenLib$wasTouchingWaterLike(WaterLikeType type) {
		return this.frozenLib$waterLikesTouching.contains(type);
	}

	@Unique
	@Override
	public List<WaterLikeType> frozenLib$touchingWaterLikeStatuses() {
		return this.frozenLib$waterLikesTouching;
	}

	@Unique
	@Override
	public boolean frozenLib$isTouchingWaterLikeOrUnderWaterAndWaterLike(WaterLikeType type) {
		return this.isUnderWater() ? this.frozenLib$wasInWaterLike(type) : this.frozenLib$wasTouchingWaterLike(type);
	}

	@Unique
	@Override
	public void frozenLib$setWaterReplacementParticlesFromBlock(@Nullable WaterLikeBlock waterLike) {
		if (waterLike == null) {
			this.frozenLib$replacementBubbleParticle = null;
			this.frozenLib$replacementSplashParticle = null;
			return;
		}

		this.frozenLib$replacementBubbleParticle = waterLike.bubbleParticle();
		this.frozenLib$replacementSplashParticle = waterLike.splashParticle();
	}

	@WrapOperation(
		method = "sendBubbleColumnParticles",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
			ordinal = 0
		)
	)
	private static int frozenLib$replaceBubbleColumnSplashParticles(
		ServerLevel instance, ParticleOptions particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed, Operation<Integer> original,
		@Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos,
		@Share("frozenLib$block") LocalRef<Block> block
	) {
		block.set(level.getBlockState(pos).getBlock());
		if (block.get() instanceof WaterLikeBlock waterLikeBlock) particle = waterLikeBlock.splashParticle();
		return original.call(instance, particle, x, y, z, count, xDist, yDist, zDist, speed);
	}

	@WrapOperation(
		method = "sendBubbleColumnParticles",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
			ordinal = 1
		)
	)
	private static int frozenLib$replaceBubbleColumnBubbleParticles(
		ServerLevel instance, ParticleOptions particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed, Operation<Integer> original,
		@Share("frozenLib$block") LocalRef<Block> block
	) {
		if (block.get() instanceof WaterLikeBlock waterLikeBlock) particle = waterLikeBlock.bubbleParticle();
		return original.call(instance, particle, x, y, z, count, xDist, yDist, zDist, speed);
	}

	@WrapOperation(
		method = "doWaterSplashEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
			ordinal = 0
		)
	)
	public void frozenLib$replaceBubbleParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original
	) {
		if (this.frozenLib$replacementBubbleParticle != null) particle = this.frozenLib$replacementBubbleParticle;
		original.call(instance, particle, x, y, z, xd, yd, zd);
	}

	@WrapOperation(
		method = "doWaterSplashEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
			ordinal = 1
		)
	)
	public void frozenLib$replaceSplashParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original
	) {
		if (this.frozenLib$replacementSplashParticle != null) particle = this.frozenLib$replacementSplashParticle;
		original.call(instance, particle, x, y, z, xd, yd, zd);
	}

	@Inject(method = "getSwimSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouchingOrUnderWaterAndWaterLike(Entity.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().genericSwimSound().value());
	}

	@Inject(method = "getSwimSplashSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimSplashSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouching(Entity.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().genericSplashSound().value());
	}

	@Inject(method = "getSwimHighSpeedSplashSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimHighSpeedSplashSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouching(Entity.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().genericSplashSound().value());
	}
}
