/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.impl.sync;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.network.ConfigSyncPacket;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;

public class ConfigSyncTask implements ConfigurationTask {
	public static final Type CONFIG_SYNC_TYPE = new Type(FrozenSharedConstants.string("config_sync"));

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
		return CONFIG_SYNC_TYPE;
	}
}
