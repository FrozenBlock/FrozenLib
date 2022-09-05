package net.frozenblock.lib.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Objects;

public class FrozenWallSignBlock extends WallSignBlock {
    public final String modId;

    public FrozenWallSignBlock(Properties settings, WoodType signType, String modId) {
        super(settings, signType);
        this.modId = modId;
    }

    @Override
    public ResourceLocation getLootTable() {
        ResourceLocation correctedLootTableId = new ResourceLocation(this.modId, "blocks/" + this.type().name() + "_sign");

        if (!Objects.equals(this.drops, correctedLootTableId)) {
            this.drops = correctedLootTableId;
        }

        return this.drops;
    }
}
