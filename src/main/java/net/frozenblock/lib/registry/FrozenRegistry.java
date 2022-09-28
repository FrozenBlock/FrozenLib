package net.frozenblock.lib.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.MappedRegistry;
import net.minecraft.sounds.SoundEvent;

public class FrozenRegistry {

    public static final MappedRegistry<SoundEvent> STARTING_SOUND = FabricRegistryBuilder.createSimple(SoundEvent.class, FrozenMain.id("starting_sound"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
}
