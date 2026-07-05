package net.frozenblock.lib.registry.mixin.neoforge;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;
import java.util.Map;

@Mixin(AxeItem.class)
public class AxeItemMixin {

	@Mutable
	@Shadow
	@Final
	protected static Map<Block, Block> STRIPPABLES;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void makeMutable(CallbackInfo ci) {
		if (!(STRIPPABLES instanceof HashMap<Block, Block>)) {
			STRIPPABLES = new Object2ObjectLinkedOpenHashMap<>(STRIPPABLES);
		}
	}
}
