package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.google.gson.JsonElement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Map;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {

	@Inject(method = "save", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addDataVersion(CallbackInfo ci, Map<ResourceLocation, AdvancementProgress> map, JsonElement jsonElement) {
		QuiltDataFixesInternals.get().addModDataVersions(jsonElement.getAsJsonObject());
	}
}
