package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ExtendedConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin implements ExtendedConnection {
	@Unique
	private Object2IntMap<String> frozenLib$modProtocol = new Object2IntOpenHashMap<>();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setDefault(PacketFlow receiving, CallbackInfo ci) {
		this.frozenLib$modProtocol.defaultReturnValue(ProtocolVersions.NO_PROTOCOL);
	}

	@Override
	public void frozenLib$setModProtocol(String id, int version) {
		this.frozenLib$modProtocol.put(id, version);
	}

	@Override
	public int frozenLib$getModProtocol(String id) {
		return this.frozenLib$modProtocol.getInt(id);
	}
}
