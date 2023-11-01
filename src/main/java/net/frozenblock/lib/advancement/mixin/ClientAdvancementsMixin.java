package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.impl.AdvancementListInteraction;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementsMixin {

	@Shadow
	@Final
	private AdvancementList advancements;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setClient(Minecraft minecraft, WorldSessionTelemetryManager telemetryManager, CallbackInfo ci) {
		((AdvancementListInteraction) this.advancements).frozenLib$setClient();
	}
}
