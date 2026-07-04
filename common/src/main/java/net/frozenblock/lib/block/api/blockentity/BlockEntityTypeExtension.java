package net.frozenblock.lib.block.api.blockentity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface BlockEntityTypeExtension {

	void frozenLib$addValidBlock(Block block);
}
