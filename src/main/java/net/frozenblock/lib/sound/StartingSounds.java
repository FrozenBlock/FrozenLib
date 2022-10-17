package net.frozenblock.lib.sound;

import java.util.HashMap;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class StartingSounds {

    /**
     * Use this to associate a Starting Sound to a {@link ResourceKey} for later use.
     */
    public static HashMap<ResourceKey<?>, SoundEvent> startingSounds = new HashMap<>();

    public static final SoundEvent EMPTY_SOUND = register("empty_sound");

    public static SoundEvent register(String key) {
        return Registry.register(FrozenRegistry.STARTING_SOUND, key, new SoundEvent(FrozenMain.id(key)));
    }

}
