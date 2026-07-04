package net.frozenblock.lib.registry.mixin.neoforge;

import java.util.Map;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {

	@Accessor("STRIPPABLES")
	static Map<Block, Block> frozenLib$getStrippables() {
		throw new AssertionError();
	}
}
