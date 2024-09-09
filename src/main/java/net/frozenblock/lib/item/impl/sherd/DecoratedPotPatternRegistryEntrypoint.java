package net.frozenblock.lib.item.impl.sherd;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface DecoratedPotPatternRegistryEntrypoint {
	void bootstrap(Registry<DecoratedPotPattern> registry);

	@Contract("_, _, _ -> new")
	static @NotNull DecoratedPotPattern register(Registry<DecoratedPotPattern> registry, ResourceKey<DecoratedPotPattern> registryKey, ResourceLocation location) {
		return Registry.register(registry, registryKey, new DecoratedPotPattern(location));
	}
}
