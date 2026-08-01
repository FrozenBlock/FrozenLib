package net.frozenblock.lib.item.mixin.loot;

import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformContainerBase.class)
public interface UniformContainerBaseAccessor {

	@Accessor("weight")
	int frozenLib$getWeight();

	@Accessor("quality")
	int frozenLib$getQuality();
}
