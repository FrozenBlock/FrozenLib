package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public interface ServerConfigurationPacketListenerAccessor {
	@Accessor("currentTask")
	@Nullable ConfigurationTask frozenLib$getCurrentTask();
}
