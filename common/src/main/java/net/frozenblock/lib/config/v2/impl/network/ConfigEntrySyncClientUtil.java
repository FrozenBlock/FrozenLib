/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.v2.impl.network;

import java.util.List;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.networking.api.ClientNetworkingHelper;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

/**
 * This class is needed for the sake of preventing {@link Minecraft#player} from loading in {@link ConfigEntrySyncPacket}.
 * <p>
 * Something about the way NeoForge handles class loading causes this to be loaded on runtime, despite the same implementation causing no issues on Fabric.
 */
@ClientOnly
@ApiStatus.Internal
public final class ConfigEntrySyncClientUtil {

	public static void sendC2S(Iterable<ConfigEntry<?>> entries) {
		if (!NetworkingHelper.connectedToServer()) return;

		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			final ConfigEntrySyncPacket<?> packet = new ConfigEntrySyncPacket<>(entry, entry.getActual());
			ClientNetworkingHelper.sendToServer(packet);
		}

		if (!FrozenLibLogUtils.UNSTABLE_LOGGING) return;

		boolean hadEntryBefore = false;
		final StringBuilder builder = new StringBuilder("ENTRY SYNC SENT ON CLIENT ENV:");
		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			builder.append(hadEntryBefore ? ", " : " ").append(entry.id());
			hadEntryBefore = true;
		}

		FrozenLibLogUtils.log(builder.toString(), FrozenLibLogUtils.UNSTABLE_LOGGING);
	}

	public static void sendC2S() {
		sendC2S(ConfigV2Registry.allConfigEntries());
	}

	public static <T> void trySendC2S(ConfigEntry<T> config) {
		trySendC2S(List.of(config));
	}

	public static void trySendC2S(Iterable<ConfigEntry<?>> entries) {
		if (!ConfigEntrySyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false)) return;
		sendC2S(entries);
	}
}
