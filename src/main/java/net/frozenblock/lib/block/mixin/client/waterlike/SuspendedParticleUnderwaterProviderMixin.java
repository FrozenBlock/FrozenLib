
package net.frozenblock.lib.block.mixin.client.waterlike;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SuspendedParticle.UnderwaterProvider.class)
public class SuspendedParticleUnderwaterProviderMixin {

	@WrapOperation(
		method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/SuspendedParticle;setColor(FFF)V"
		)
	)
	public void frozenLib$changeColorToWaterLike(
		SuspendedParticle instance, float r, float g, float b, Operation<Void> original,
		SimpleParticleType options, ClientLevel level, double x, double y, double z
	) {
		final BlockState state = level.getBlockState(BlockPos.containing(x, y, z));
		if (state.getBlock() instanceof WaterLikeBlock waterLikeBlock) {
			final int mesogleaColor = waterLikeBlock.waterFogColor().rgba();
			r = ARGB.red(mesogleaColor) / 255F;
			g = ARGB.green(mesogleaColor) / 255F;
			b  = ARGB.blue(mesogleaColor) / 255F;
		}

		original.call(instance, r, g, b);
	}

}
