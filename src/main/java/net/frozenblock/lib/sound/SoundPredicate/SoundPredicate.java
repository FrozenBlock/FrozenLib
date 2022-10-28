package net.frozenblock.lib.sound.SoundPredicate;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class SoundPredicate<T extends Entity> {

    public static <T extends Entity> void register(ResourceLocation id, LoopPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE_SYNCED, id, new SoundPredicate<>(predicate));
    }

	public static <T extends Entity> void registerUnsynced(ResourceLocation id, LoopPredicate<T> predicate) {
		Registry.register(FrozenRegistry.SOUND_PREDICATE, id, new SoundPredicate<>(predicate));
	}

	private final LoopPredicate<T> predicate;

	public SoundPredicate(LoopPredicate<T> predicate) {
		this.predicate = predicate;
	}

    public static <T extends Entity> LoopPredicate<T> getPredicate(@Nullable ResourceLocation id) {
        if (id != null) {
            if (FrozenRegistry.SOUND_PREDICATE_SYNCED.containsKey(id)) {
				SoundPredicate<T> predicate = FrozenRegistry.SOUND_PREDICATE_SYNCED.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			} else if (FrozenRegistry.SOUND_PREDICATE.containsKey(id)) {
				SoundPredicate<T> predicate = FrozenRegistry.SOUND_PREDICATE.get(id);
				if (predicate != null) {
					return predicate.predicate;
				}
			}
			FrozenMain.LOGGER.error("Unable to find sound predicate " + id + "! Using default sound predicate instead!");
        }
        return defaultPredicate();
    }

    @FunctionalInterface
    public interface LoopPredicate<T extends Entity> {
        boolean test(T entity);

		default void onStop(T entity) {

		}
    }

	public static <T extends Entity> LoopPredicate<T> defaultPredicate() {
		return entity -> !entity.isSilent();
	}
    public static ResourceLocation DEFAULT_ID = FrozenMain.id("default");
	public static <T extends Entity> LoopPredicate<T> notSilentAndAlive() {
		return entity -> !entity.isSilent();
	}
    public static ResourceLocation NOT_SILENT_AND_ALIVE_ID = FrozenMain.id("not_silent_and_alive");

    public static void init() {
        register(FrozenMain.id("default"), defaultPredicate());
        register(FrozenMain.id("not_silent_and_alive"), notSilentAndAlive());
    }
}
