package net.frozenblock.lib.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.entity.render.EasterEgg;
import net.frozenblock.lib.sound.SoundPredicate.FrozenSoundPredicate;
import net.minecraft.core.MappedRegistry;
import net.minecraft.sounds.SoundEvent;

public class FrozenRegistry {

    public static final MappedRegistry<SoundEvent> STARTING_SOUND = FabricRegistryBuilder.createSimple(SoundEvent.class, FrozenMain.id("starting_sound"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final MappedRegistry<EasterEgg> EASTER_EGG = FabricRegistryBuilder.createSimple(EasterEgg.class, FrozenMain.id("easter_egg"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

	public static final MappedRegistry<FrozenSoundPredicate> SOUND_PREDICATES = FabricRegistryBuilder.createSimple(FrozenSoundPredicate.class, FrozenMain.id("sound_predicates"))
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

    public static void initRegistry() {

    }
}
