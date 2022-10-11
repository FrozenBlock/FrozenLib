package net.frozenblock.lib.sound;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class FrozenSoundPredicates {
    public static Map<ResourceLocation, LoopPredicate<?>> predicates =
            new HashMap<>();

    public static void register(ResourceLocation id,
                                LoopPredicate<?> predicate) {
        predicates.put(id, predicate);
    }

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
    public static ResourceLocation DEFAULT_ID = FrozenMain.id("default");
    public static LoopPredicate<Entity> NOT_SILENT_AND_ALIVE =
            entity -> !entity.isSilent();
    public static ResourceLocation NOT_SILENT_AND_ALIVE_ID =
            FrozenMain.id("not_silent_and_alive");

    public static void init() {
        register(FrozenMain.id("default"), DEFAULT);
        register(FrozenMain.id("not_silent_and_alive"), NOT_SILENT_AND_ALIVE);
    }
}
