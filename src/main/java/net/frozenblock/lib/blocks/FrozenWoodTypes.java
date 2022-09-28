package net.frozenblock.lib.blocks;

import net.minecraft.world.level.block.state.properties.WoodType;

public final class FrozenWoodTypes {

    public static WoodType newType(String name) {
        return new WoodType(name);
    }

    public static WoodType register(WoodType woodType) {
        return WoodType.register(woodType);
    }
}
