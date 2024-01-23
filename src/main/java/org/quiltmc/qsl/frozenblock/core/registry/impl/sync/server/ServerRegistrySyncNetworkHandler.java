/*
 * Copyright 2023-2024 The Quilt Project
 * Copyright 2023-2024 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server;

import com.mojang.logging.LogUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * All the magic happens here!
 * <p>
 * This is special PacketListener for handling registry sync.
 * Why does it exist? Wouldn't usage of login packets be better?
 * <p>
 * And well, yes it would, but sadly these can't be made compatible with proxy
 * software like Velocity (see Forge). Thankfully emulating them on PLAY
 * protocol isn't too hard and gives equal results. And doing them on PLAY
 * is needed for Fabric compatibility anyway.
 * It still doesn't work with Velocity out of the box (they don't care much about this
 * being valid), getting support is still simple.
 */
@ApiStatus.Internal
public final class ServerRegistrySyncNetworkHandler implements ServerGamePacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final int HELLO_PING = 0;
	private static final int GOODBYE_PING = 1;

	private final Connection connection;
	private final ExtendedConnection extendedConnection;
	private final ServerPlayer player;
	private final Runnable continueLoginRunnable;

	private final List<ServerboundCustomPayloadPacket> delayedPackets = new ArrayList<>();
	private int syncVersion = ProtocolVersions.NO_PROTOCOL;

	public ServerRegistrySyncNetworkHandler(ServerPlayer player, Connection connection, Runnable continueLogin) {
		this.connection = connection;
		this.player = player;
		this.continueLoginRunnable = continueLogin;
		this.extendedConnection = (ExtendedConnection) connection;

		((DelayedPacketsHolder) this.player).frozenLib$setPacketList(this.delayedPackets);

		ServerRegistrySync.sendHelloPacket(connection);
		connection.send(new ClientboundPingPacket(HELLO_PING));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handlePong(ServerboundPongPacket packet) {
		switch (packet.getId()) {
			case HELLO_PING -> {
				if (ServerRegistrySync.SERVER_SUPPORTED_PROTOCOL.contains(this.syncVersion)) {
					ServerRegistrySync.sendSyncPackets(this.connection, this.player, this.syncVersion);
				}

				this.connection.send(new ClientboundPingPacket(GOODBYE_PING));
			}
			case GOODBYE_PING -> {
				if (this.syncVersion == ProtocolVersions.NO_PROTOCOL && ServerRegistrySync.requiresSync()) {
					this.disconnect(ServerRegistrySync.noRegistrySyncMessage);
				} else {
					this.continueLogin();
				}
			}
		}
	}

	private void continueLogin() {
		this.player.server.execute(this.continueLoginRunnable);
	}

	@Override
	public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {
		if (packet.getIdentifier().equals(ClientPackets.Handshake.PACKET_TYPE.getId())) {
			this.syncVersion = packet.getData().readVarInt();
		} else if (packet.getIdentifier().equals(ClientPackets.ModProtocol.PACKET_TYPE.getId())) {
			this.handleModProtocol(packet.getData());
		} else {
			this.delayedPackets.add(new ServerboundCustomPayloadPacket(packet.getIdentifier(), new FriendlyByteBuf(packet.getData().copy())));
		}
	}

	private void handleModProtocol(FriendlyByteBuf data) {
		var count = data.readVarInt();
		while (count-- > 0) {
			var id = data.readUtf();
			var version = data.readVarInt();
			this.extendedConnection.frozenLib$setModProtocol(id, version);
		}
	}

	@Override
	public void onDisconnect(Component reason) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());

		for (var packet : this.delayedPackets) {
			if (packet.getData().refCnt() != 0) {
				packet.getData().release(packet.getData().refCnt());
			}
		}
	}

	public void disconnect(Component reason) {
		try {
			for (var packet : this.delayedPackets) {
				if (packet.getData().refCnt() != 0) {
					packet.getData().release(packet.getData().refCnt());
				}
			}

			this.connection.send(new ClientboundDisconnectPacket(reason),
				PacketSendListener.thenRun(() -> this.connection.disconnect(reason))
			);
		} catch (Exception var3) {
			LOGGER.error("Error whilst disconnecting player", var3);
		}
	}

	@Override
	public void handleAnimate(ServerboundSwingPacket packet) {

	}

	@Override
	public void handleChat(ServerboundChatPacket packet) {

	}

	@Override
	public void handleChatCommand(ServerboundChatCommandPacket packet) {

	}

	@Override
	public void handleChatAck(ServerboundChatAckPacket packet) {

	}

	@Override
	public void handleClientCommand(ServerboundClientCommandPacket packet) {

	}

	@Override
	public void handleClientInformation(ServerboundClientInformationPacket packet) {

	}

	@Override
	public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {

	}

	@Override
	public void handleContainerClick(ServerboundContainerClickPacket packet) {

	}

	@Override
	public void handlePlaceRecipe(ServerboundPlaceRecipePacket packet) {

	}

	@Override
	public void handleContainerClose(ServerboundContainerClosePacket packet) {

	}

	@Override
	public void handleInteract(ServerboundInteractPacket packet) {

	}

	@Override
	public void handleKeepAlive(ServerboundKeepAlivePacket packet) {

	}

	@Override
	public void handleMovePlayer(ServerboundMovePlayerPacket packet) {

	}

	@Override
	public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {

	}

	@Override
	public void handlePlayerAction(ServerboundPlayerActionPacket packet) {

	}

	@Override
	public void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {

	}

	@Override
	public void handlePlayerInput(ServerboundPlayerInputPacket packet) {

	}

	@Override
	public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {

	}

	@Override
	public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {

	}

	@Override
	public void handleSignUpdate(ServerboundSignUpdatePacket packet) {

	}

	@Override
	public void handleUseItemOn(ServerboundUseItemOnPacket packet) {

	}

	@Override
	public void handleUseItem(ServerboundUseItemPacket packet) {

	}

	@Override
	public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {

	}

	@Override
	public void handleResourcePackResponse(ServerboundResourcePackPacket packet) {

	}

	@Override
	public void handlePaddleBoat(ServerboundPaddleBoatPacket packet) {

	}

	@Override
	public void handleMoveVehicle(ServerboundMoveVehiclePacket packet) {

	}

	@Override
	public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packet) {

	}

	@Override
	public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket packet) {

	}

	@Override
	public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket packet) {

	}

	@Override
	public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket packet) {

	}

	@Override
	public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {

	}

	@Override
	public void handleSetCommandBlock(ServerboundSetCommandBlockPacket packet) {

	}

	@Override
	public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket packet) {

	}

	@Override
	public void handlePickItem(ServerboundPickItemPacket packet) {

	}

	@Override
	public void handleRenameItem(ServerboundRenameItemPacket packet) {

	}

	@Override
	public void handleSetBeaconPacket(ServerboundSetBeaconPacket packet) {

	}

	@Override
	public void handleSetStructureBlock(ServerboundSetStructureBlockPacket packet) {

	}

	@Override
	public void handleSelectTrade(ServerboundSelectTradePacket packet) {

	}

	@Override
	public void handleEditBook(ServerboundEditBookPacket packet) {

	}

	@Override
	public void handleEntityTagQuery(ServerboundEntityTagQuery packet) {

	}

	@Override
	public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery packet) {

	}

	@Override
	public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket packet) {

	}

	@Override
	public void handleJigsawGenerate(ServerboundJigsawGeneratePacket packet) {

	}

	@Override
	public void handleChangeDifficulty(ServerboundChangeDifficultyPacket packet) {

	}

	@Override
	public void handleLockDifficulty(ServerboundLockDifficultyPacket packet) {

	}

	@Override
	public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket packet) {

	}

	@Override
	public boolean isAcceptingMessages() {
		return this.connection.isConnected();
	}
}
