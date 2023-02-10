package net.frozenblock.lib.config.api.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.core.MappedRegistry;

public class ConfigRegistry {

	public static final MappedRegistry<Config> CONFIG = FabricRegistryBuilder.createSimple(Config.class, FrozenMain.id("config"))
			.attribute(RegistryAttribute.MODDED)
			.buildAndRegister();

	public static final MappedRegistry<TypedEntry> TYPED_ENTRY = FabricRegistryBuilder.createSimple(TypedEntry.class, FrozenMain.id("typed_entry"))
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();
}
