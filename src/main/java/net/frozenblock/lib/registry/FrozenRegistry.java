package net.frozenblock.lib.registry;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.sound.StartingSound;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class FrozenRegistry {

    public static final ResourceKey<Registry<StartingSound>> STARTING_SOUND_REGISTRY = createRegistryKey("starting_sound");
    public static final Registry<StartingSound> STARTING_SOUND = Registry.registerSimple(STARTING_SOUND_REGISTRY, registry -> StartingSound.EMPTY_SOUND);

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String registryName) {
        return ResourceKey.createRegistryKey(FrozenMain.id(registryName));
    }
}
