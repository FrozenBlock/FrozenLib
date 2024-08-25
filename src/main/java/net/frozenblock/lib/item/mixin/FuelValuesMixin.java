package net.frozenblock.lib.item.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.item.api.FuelRegistry;
import net.minecraft.world.level.block.entity.FuelValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FuelValues.class)
public class FuelValuesMixin {

	@WrapOperation(
		method = "vanillaBurnTimes(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/flag/FeatureFlagSet;I)Lnet/minecraft/world/level/block/entity/FuelValues;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/FuelValues$Builder;build()Lnet/minecraft/world/level/block/entity/FuelValues;")
	)
	private static FuelValues addModdedItems(FuelValues.Builder instance, Operation<FuelValues> original) {
		for (FuelRegistry.ItemFuelValue value : FuelRegistry.ITEM_FUEL_VALUES) {
			instance.add(value.item(), value.time());
		}
		for (FuelRegistry.TagFuelValue value : FuelRegistry.TAG_FUEL_VALUES) {
			instance.add(value.tag(), value.time());
		}
		return original.call(instance);
	}
}
