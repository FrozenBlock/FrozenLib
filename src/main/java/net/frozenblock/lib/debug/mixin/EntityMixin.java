package net.frozenblock.lib.debug.mixin;

import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.debug.networking.GoalDebugRemovePayload;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow
	private Level level;
	@Shadow
	private int id;

	@Inject(method = "remove", at = @At("HEAD"))
	public void devTools$remove(Entity.RemovalReason reason, CallbackInfo info) {
		if (this.level instanceof ServerLevel serverLevel && FrozenLibConfig.IS_DEBUG) {
			FrozenNetworking.sendPacketToAllPlayers(
				serverLevel,
				new GoalDebugRemovePayload(this.id)
			);
		}
	}

}
