package net.frozenblock.lib.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.entity.render.EntityTextureOverride;
import net.frozenblock.lib.sound.SoundPredicate.SoundPredicate;
import net.minecraft.core.MappedRegistry;
import net.minecraft.sounds.SoundEvent;

public class FrozenRegistry {

    public static final MappedRegistry<SoundEvent> STARTING_SOUND = FabricRegistryBuilder.createSimple(SoundEvent.class, FrozenMain.id("starting_sound"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final MappedRegistry<EntityTextureOverride> ENTITY_TEXTURE_OVERRIDE = FabricRegistryBuilder.createSimple(EntityTextureOverride.class, FrozenMain.id("entity_texture_override"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

	public static final MappedRegistry<SoundPredicate> SOUND_PREDICATE_SYNCED = FabricRegistryBuilder.createSimple(SoundPredicate.class, FrozenMain.id("sound_predicate_synced"))
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final MappedRegistry<SoundPredicate> SOUND_PREDICATE = FabricRegistryBuilder.createSimple(SoundPredicate.class, FrozenMain.id("sound_predicate"))
			.buildAndRegister();

    public static void initRegistry() {

    }
}
