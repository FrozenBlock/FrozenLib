package net.frozenblock.lib.block.mixin.client.fire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.concurrent.atomic.AtomicReference;
import net.frozenblock.lib.block.api.fire.FireTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WallTorchBlock.class)
public class WallTorchBlockMixin {

	@WrapOperation(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
			ordinal = 0
		)
	)
	public void frozenLib$coloredSmokeParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, Operation<Void> original,
		@Local(argsOnly = true) BlockState state
	) {
		final AtomicReference<ParticleOptions> atomicParticleOptions = new AtomicReference<>(particle);
		FireTypes.getTypeHolderForBlock(instance.registryAccess(), state.getBlock(), true).ifPresent(fireTypeHolder -> {
			atomicParticleOptions.set(fireTypeHolder.value().particleSettings().getSmokeParticle(particle));
		});

		original.call(instance, atomicParticleOptions.get(), x, y, z, xd, yd, zd);
	}
}
