package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.api.AdvancementEvents;
import net.frozenblock.lib.advancement.impl.AdvancementListInteraction;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Iterator;
import java.util.Map;

@Mixin(AdvancementList.class)
public class AdvancementListMixin implements AdvancementListInteraction {

	@Unique
	private boolean isClient = false;

	@Inject(method = "add", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void modifyAdvancements(Map<ResourceLocation, Advancement.Builder> advancements, CallbackInfo ci, Map map, boolean bl, Iterator iterator, Map.Entry entry, ResourceLocation resourceLocation, Advancement.Builder builder, Advancement advancement) {
		if (!isClient)
			AdvancementEvents.INIT.invoker().onInit(new AdvancementEvents.AdvancementHolder(resourceLocation, advancement));
	}

	@Unique
	@Override
	public void frozenLib$setClient() {
		isClient = true;
	}
}
