package net.frozenblock.lib.sound;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;

public class StartingSound extends SoundEvent {

    public static HashMap<ResourceKey<?>, StartingSound> startingSounds = new HashMap<>();

    public static final StartingSound EMPTY_SOUND = register("empty_sound");

    public StartingSound(ResourceLocation location) {
        super(location);
    }

    private StartingSound(SoundEvent soundEvent) {
        this(soundEvent.getLocation());
    }

    public static StartingSound of(SoundEvent soundEvent) {
        return new StartingSound(soundEvent);
    }

    public static StartingSound register(String key) {
        return Registry.register(FrozenRegistry.STARTING_SOUND, key, new StartingSound(FrozenMain.id(key)));
    }

}
