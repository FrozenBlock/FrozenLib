package net.frozenblock.lib.sound;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class RegisterMovingSoundRestrictions {
    public static Map<ResourceLocation, LoopPredicate<?>> predicates = new HashMap<>();

    public static void register(ResourceLocation id, LoopPredicate<?> predicate) {
        predicates.put(id, predicate);
    }

    @Nullable
    public static LoopPredicate<?> getPredicate(@Nullable ResourceLocation id) {
        if (id != null) {
            if (predicates.containsKey(id)) {
                return predicates.get(id);
            }
        }
        return DEFAULT;
    }

    @FunctionalInterface
    public interface LoopPredicate<T extends Entity> {
        boolean test(Entity entity);
    }

    public static LoopPredicate<Entity> DEFAULT = entity -> !entity.isSilent();

    public static void init() {
        register(FrozenMain.id("default"), DEFAULT);
    }
}
