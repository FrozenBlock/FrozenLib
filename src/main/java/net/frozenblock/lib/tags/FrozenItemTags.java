package net.frozenblock.lib.tags;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class FrozenItemTags {
    public static final TagKey<Item> NO_USE_GAME_EVENTS =
            of("dont_emit_use_game_events");

    private FrozenItemTags() {
    }

    private static TagKey<Item> of(String path) {
        return TagKey.create(Registry.ITEM_REGISTRY, FrozenMain.id(path));
    }
}
