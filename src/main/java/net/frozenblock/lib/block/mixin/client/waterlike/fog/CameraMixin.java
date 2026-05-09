package net.frozenblock.lib.block.mixin.client.waterlike.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.impl.waterlike.WaterLikeFogUtil;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow
	private Level level;

	@Shadow
	public abstract FogType getFluidInCamera();

	@Shadow
	public abstract BlockPos blockPosition();

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/EnvironmentAttributeProbe;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;)V"
		)
	)
	private void frozenLib$tickWaterLikeFogHandler(CallbackInfo info) {
		WaterLikeFogUtil.tick(this.level, this.blockPosition(), this.getFluidInCamera(), false);
	}

	@Inject(method = "reset", at = @At("HEAD"))
	private void frozenLib$resetWaterLikeFog(CallbackInfo info) {
		WaterLikeFogUtil.reset(true);
	}

}
