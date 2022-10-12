package net.frozenblock.lib.tags;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class FrozenEntityTags {
    public static final TagKey<EntityType<?>> CREEPER_IGNORES = bind("creeper_ignores");

    private FrozenEntityTags() {
    }

    private static TagKey<EntityType<?>> bind(String path) {
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, FrozenMain.id(path));
    }
}
