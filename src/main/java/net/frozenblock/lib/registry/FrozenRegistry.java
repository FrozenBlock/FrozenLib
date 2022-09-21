package net.frozenblock.lib.registry;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.sound.StartingSounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class FrozenRegistry {

    public static final ResourceKey<Registry<SoundEvent>> STARTING_SOUND_REGISTRY = createRegistryKey("starting_sound");
    public static final Registry<SoundEvent> STARTING_SOUND = Registry.registerSimple(STARTING_SOUND_REGISTRY, registry -> StartingSounds.EMPTY_SOUND);

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String registryName) {
        return ResourceKey.createRegistryKey(FrozenMain.id(registryName));
    }
}
