package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import net.minecraft.core.MappedRegistry;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DelayedRegistryImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> implements DelayedRegistryImpl {

	@Shadow
	private boolean frozen;

	@Unique
	@Override
	public void setFrozen(boolean bl) {
		this.frozen = bl;
	}
}
