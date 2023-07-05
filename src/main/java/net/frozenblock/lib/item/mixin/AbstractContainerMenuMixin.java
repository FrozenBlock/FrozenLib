package net.frozenblock.lib.item.mixin;

import net.frozenblock.lib.item.impl.ItemStackExtension;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.function.Supplier;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

	@Inject(method = "triggerSlotListeners", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void triggerSlotListeners(int slotIndex, ItemStack stack, Supplier<ItemStack> supplier, CallbackInfo ci, ItemStack itemStack) {
		ItemStackExtension.class.cast(itemStack).frozenLib$setInContainer(true);
	}
}
