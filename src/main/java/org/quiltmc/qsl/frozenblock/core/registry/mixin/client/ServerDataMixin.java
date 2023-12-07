package org.quiltmc.qsl.frozenblock.core.registry.mixin.client;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ServerData;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.mod_protocol.ModProtocolContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ServerData.class)
public class ServerDataMixin implements ModProtocolContainer {
	@Unique
	private Map<String, IntList> frozenLib$modProtocol;

	@Override
	public void frozenLib$setModProtocol(Map<String, IntList> map) {
		this.frozenLib$modProtocol = map;
	}

	@Override
	public Map<String, IntList> frozenLib$getModProtocol() {
		return this.frozenLib$modProtocol;
	}
}
