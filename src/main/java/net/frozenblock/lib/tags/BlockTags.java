package net.frozenblock.lib.tags;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class BlockTags {
    public static final TagKey<Block> DRIPSTONE_CAN_DRIP_ON = of("dripstone_can_drip_on");

    private BlockTags() {
    }

    private static TagKey<Block> of(String path) {
        return TagKey.create(Registry.BLOCK_REGISTRY, FrozenMain.id(path));
    }
}
