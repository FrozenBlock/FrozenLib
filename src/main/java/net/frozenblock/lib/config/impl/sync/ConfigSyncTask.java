package net.frozenblock.lib.config.impl.sync;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.network.ConfigSyncPacket;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import java.util.function.Consumer;

public class ConfigSyncTask implements ConfigurationTask {
	public static final Type TYPE = new Type(FrozenMain.string("config_sync"));

	@Override
	public void start(Consumer<Packet<?>> sender) {
		for (Config<?> config : ConfigRegistry.getAllConfigs()) {
			if (!config.supportsModification()) continue;
			ConfigSyncPacket<?> packet = new ConfigSyncPacket<>(config.modId(), config.configClass().getName(), config.config());
			sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
		}
	}

	@Override
	public Type type() {
		return TYPE;
	}
}
