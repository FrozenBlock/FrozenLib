package org.quiltmc.qsl.frozenblock.core.registry.impl.event;

import net.minecraft.core.WritableRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FabricDelayedRegistry<T> extends DelayedRegistry<T> {

	public FabricDelayedRegistry(WritableRegistry<T> registry) {
		super(registry);
	}
}
