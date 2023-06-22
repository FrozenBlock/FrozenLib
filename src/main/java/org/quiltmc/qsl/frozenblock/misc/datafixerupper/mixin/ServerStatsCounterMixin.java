package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.google.gson.JsonObject;
import net.minecraft.stats.ServerStatsCounter;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Map;

@Mixin(ServerStatsCounter.class)
public class ServerStatsCounterMixin {

	@Inject(method = "toJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Number;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addDataVersion(CallbackInfoReturnable<String> cir, Map map, JsonObject unused, JsonObject jsonObject) {
		QuiltDataFixesInternals.get().addModDataVersions(jsonObject);
	}
}
