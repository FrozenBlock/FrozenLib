package net.frozenblock.lib.entity.mixin.cubemob.sulfurcube;

import net.frozenblock.lib.entity.api.cubemob.sulfurcube.SulfurCubeEvents;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCubeMob.class)
public class AbstractCubeMobMixin {

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;getSquishSound()Lnet/minecraft/sounds/SoundEvent;"
		)
	)
	public void frozenLib$onSulfurCubeSquish(CallbackInfo info) {
		if (AbstractCubeMob.class.cast(this) instanceof SulfurCube sulfurCube) SulfurCubeEvents.ON_SQUISH.invoker().onSquish(sulfurCube);
	}
}
