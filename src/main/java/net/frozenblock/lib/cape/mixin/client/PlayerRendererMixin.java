package net.frozenblock.lib.cape.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.cape.client.impl.PlayerCapeInterface;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

	@Inject(method = "extractCapeState", at = @At("TAIL"))
	private static void frozenLib$extractCapeState(
		AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci
	) {
		if (playerRenderState instanceof PlayerCapeInterface capeInterface) {
			if (abstractClientPlayer instanceof PlayerCapeInterface playerCapeInterface) {
				capeInterface.frozenLib$setCape(playerCapeInterface.frozenLib$getCape());
			}
		}
	}
}
