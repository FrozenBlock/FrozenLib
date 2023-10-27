package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.api.AdvancementEvents;
import net.frozenblock.lib.advancement.impl.AdvancementContextImpl;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementHolder.class)
public class AdvancementHolderMixin {

	@Inject(method = "read", at = @At("RETURN"))
	private static void modifyAdvancement(FriendlyByteBuf buf, CallbackInfoReturnable<AdvancementHolder> cir) {
		//AdvancementEvents.INIT.invoker().onInit(new AdvancementContextImpl(cir.getReturnValue()));
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(ResourceLocation resourceLocation, Advancement advancement, CallbackInfo ci) {
		AdvancementEvents.INIT.invoker().onInit(new AdvancementContextImpl((AdvancementHolder) (Object) this));
	}
}
