package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.item.impl.FrozenTabs;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin implements FrozenTabs {

	private CreativeModeTab.ItemDisplayBuilder displayBuilder;
	private FeatureFlagSet featureFlagSet;

	@Inject(method = "lazyBuildDisplayItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;generateDisplayItems(Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/world/item/CreativeModeTab$Output;)V"))
	private void setDisplayBuilder(FeatureFlagSet featureFlagSet, boolean bl, CallbackInfoReturnable<ItemStackLinkedSet> cir) {

	}

	@Override
	public CreativeModeTab.ItemDisplayBuilder getDisplayBuilder() {
		return this.displayBuilder;
	}

	@Override
	public FeatureFlagSet getFeatureFlagSet() {
		return this.featureFlagSet;
	}
}
