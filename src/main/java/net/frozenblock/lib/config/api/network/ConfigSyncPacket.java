package net.frozenblock.lib.config.api.network;

import blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record ConfigSyncPacket<T>(
	String modId,
	String className,
	T configData
) implements FabricPacket {
	public static final PacketType<ConfigSyncPacket<?>> PACKET_TYPE = PacketType.create(FrozenMain.CONFIG_SYNC_PACKET, ConfigSyncPacket::create);

	@Nullable
	public static <T> ConfigSyncPacket<T> create(FriendlyByteBuf buf) {
		String modId = buf.readUtf();
		String className = buf.readUtf();
		try {
			T configData = ConfigByteBufUtil.readJankson(buf, modId, className);
			return new ConfigSyncPacket<>(modId, className, configData);
		} catch (SyntaxError | ClassNotFoundException e) {
			FrozenLogUtils.error("Failed to read config data from packet.", true, e);
			return null;
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(modId);
		buf.writeUtf(className);
		ConfigByteBufUtil.writeJankson(buf, modId, configData);
	}

	public static <T> void receive(ConfigSyncPacket<T> packet) {
		String modId = packet.modId();
		String className = packet.className();
        for (Config<?> raw : ConfigRegistry.getConfigsForMod(modId)) {
			String configClassName = raw.configClass().getName();
            if (!configClassName.equals(className)) continue;
			Config<T> config = (Config<T>) raw;
			boolean shouldAddModification = !ConfigRegistry.containsSyncData(config);
			ConfigRegistry.setSyncData(config, new ConfigSyncData<>(packet.configData()));
            if (shouldAddModification) {
				ConfigRegistry.register(
					config,
					new ConfigModification<>(
						new ConfigSyncModification<>(config, ConfigRegistry::getSyncData)
					),
					Integer.MIN_VALUE // make sure its set first
				);
			}
			break;
        }
    }

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
