package net.frozenblock.lib.block.mixin.client.waterlike;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(
		method = "extractShadowPiece",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;)I",
			shift = At.Shift.BEFORE
		),
		cancellable = true,
		require = 0
	)
	private static void frozenLib$stopShadowRenderingIfWaterLike(
		CallbackInfo info,
		@Local(name = "belowState") BlockState belowState
	) {
		if (belowState.getBlock() instanceof WaterLikeBlock) info.cancel();
	}

}
