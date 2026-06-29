package net.frozenblock.lib.networking;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ConfigPacketSender {

	void sendPacket(CustomPacketPayload payload);

	void disconnect(Component reason);
}
