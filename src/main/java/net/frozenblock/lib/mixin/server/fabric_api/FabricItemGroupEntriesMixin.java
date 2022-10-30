package net.frozenblock.lib.mixin.server.fabric_api;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.frozenblock.lib.feature_flag.impl.FabricItemGroupAccessor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(FabricItemGroupEntries.class)
public abstract class FabricItemGroupEntriesMixin implements FabricItemGroupAccessor {
	@Shadow
	protected abstract boolean isEnabled(ItemStack stack);

	@Override
	public boolean enabled(ItemStack itemStack) {
		return this.isEnabled(itemStack);
	}
}
