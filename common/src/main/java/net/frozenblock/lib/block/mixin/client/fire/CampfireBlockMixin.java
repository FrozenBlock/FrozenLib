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

package net.frozenblock.lib.block.mixin.client.fire;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

	@ModifyExpressionValue(
		method = "animateTick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/level/block/CampfireBlock;spawnParticles:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	public boolean frozenLib$enableLavaIfDisabled(
		boolean original,
		@Local(argsOnly = true) BlockState state,
		@Local(argsOnly = true) Level level,
		@Share("frozenLib$optionalFireType") LocalRef<Optional<Holder<FireType>>> optionalFireType
	) {
		optionalFireType.set(FireTypes.getTypeHolderForBlock(level.registryAccess(), state.getBlock(), true));
		return optionalFireType.get().map(fireTypeHolder -> fireTypeHolder.value().particleSettings().lavaEnabled()).orElse(original);
	}

	@WrapOperation(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		)
	)
	public void frozenLib$coloredLavaParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original,
		@Share("frozenLib$optionalFireType") LocalRef<Optional<Holder<FireType>>> optionalFireType
	) {
		final AtomicReference<ParticleOptions> atomicParticleOptions = new AtomicReference<>(particle);
		optionalFireType.get().ifPresent(fireTypeHolder -> {
			atomicParticleOptions.set(fireTypeHolder.value().particleSettings().getLavaParticle(particle));
		});

		original.call(instance, atomicParticleOptions.get(), x, y, z, xd, yd, zd);
	}

	@Inject(method = "makeParticles", at = @At("HEAD"))
	private static void frozenLib$setupMakeParticlesFireType(
		Level level, BlockPos pos, boolean isSignalFire, boolean smoking, CallbackInfo info,
		@Share("frozenLib$optionalFireType") LocalRef<Optional<Holder<FireType>>> optionalFireType
	) {
		optionalFireType.set(FireTypes.getTypeHolderForBlock(level.registryAccess(), level.getBlockState(pos).getBlock(), true));
	}

	@WrapOperation(
		method = "makeParticles",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V"
		)
	)
	private static void frozenLib$coloredCampfireSmokeParticles(
		Level instance, ParticleOptions particle, boolean overrideLimiter, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original,
		@Local(argsOnly = true, ordinal = 0) boolean isSignalFire,
		@Share("frozenLib$optionalFireType") LocalRef<Optional<Holder<FireType>>> optionalFireType
	) {
		final AtomicReference<ParticleOptions> atomicParticleOptions = new AtomicReference<>(particle);
		optionalFireType.get().ifPresent(fireTypeHolder -> {
			final FireType.ParticleSettings particleSettings = fireTypeHolder.value().particleSettings();
			atomicParticleOptions.set(isSignalFire ? particleSettings.getCampfireSignalSmokeParticle(particle) : particleSettings.getCampfireCosySmokeParticle(particle));
		});

		original.call(instance, atomicParticleOptions.get(), overrideLimiter, x, y, z, xd, yd, zd);
	}

	@WrapOperation(
		method = "makeParticles",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		)
	)
	private static void frozenLib$coloredSmokeParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original,
		@Share("frozenLib$optionalFireType") LocalRef<Optional<Holder<FireType>>> optionalFireType
	) {
		final AtomicReference<ParticleOptions> atomicParticleOptions = new AtomicReference<>(particle);
		optionalFireType.get().ifPresent(fireTypeHolder -> {
			atomicParticleOptions.set(fireTypeHolder.value().particleSettings().getSmokeParticle(particle));
		});

		original.call(instance, atomicParticleOptions.get(), x, y, z, xd, yd, zd);
	}
}
