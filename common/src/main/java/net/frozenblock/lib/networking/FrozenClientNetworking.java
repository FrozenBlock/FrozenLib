package net.frozenblock.lib.networking;

import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.platform.api.ClientOnly;

@ClientOnly
public final class FrozenClientNetworking {

	public static void registerClientReceivers() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, minecraft, player) ->
			ConfigEntrySyncPacket.receive(packet, null, null)
		);
		ClientConnectionEvents.DISCONNECT.register((handler, client) -> {
			for (ConfigEntry<?> config : ConfigV2Registry.allConfigEntries()) ConfigSyncModification.clearSyncData(config);
		});
	}
}
