package net.frozenblock.lib.block.impl.clipgroup;

import net.minecraft.world.level.block.state.BlockState;

public interface ClipGroupInterface {
	void frozenLib$setClipInGroup(ClipGroup group, boolean inside);
	boolean frozenLib$wasClipInGroup(ClipGroup group);
	boolean frozenLib$wasClipInGroup(BlockState state);
}
