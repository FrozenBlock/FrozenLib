package net.frozenblock.lib.cape.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.cape.client.impl.AbstractClientPlayerCapeInterface;
import net.frozenblock.lib.cape.networking.CapeCustomizePacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FrozenClientCapeData {
	private static final Map<UUID, ResourceLocation> CAPES_IN_SERVER = new HashMap<>();

	public static Optional<ResourceLocation> getCapeTexture(UUID uuid) {
		return Optional.ofNullable(CAPES_IN_SERVER.get(uuid));
	}

	public static void setCapeForUUID(UUID uuid, ResourceLocation texture) {
		CAPES_IN_SERVER.put(uuid, texture);
		setPlayerCapeTexture(uuid, texture);
	}

	public static void removeCapeForUUID(UUID uuid) {
		CAPES_IN_SERVER.remove(uuid);
		setPlayerCapeTexture(uuid, null);
	}

	private static void setPlayerCapeTexture(UUID uuid, @Nullable ResourceLocation texture) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null && level.getPlayerByUUID(uuid) instanceof AbstractClientPlayerCapeInterface capeInterface) {
			capeInterface.frozenLib$setCape(texture);
		}
	}

	public static void init() {
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> CAPES_IN_SERVER.clear());
		ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> CAPES_IN_SERVER.clear());
		ClientPlayConnectionEvents.JOIN.register((clientPacketListener, packetSender, minecraft) -> {
			ClientPlayNetworking.send(CapeCustomizePacket.createPacket(minecraft.getUser().getProfileId(), FrozenLibConfig.get().cape.texture()));
		});
		ClientEntityEvents.ENTITY_LOAD.register((entity, clientLevel) -> {
			if (entity instanceof AbstractClientPlayerCapeInterface capeInterface) {
				getCapeTexture(entity.getUUID()).ifPresent(capeInterface::frozenLib$setCape);
			}
		});
	}
}
