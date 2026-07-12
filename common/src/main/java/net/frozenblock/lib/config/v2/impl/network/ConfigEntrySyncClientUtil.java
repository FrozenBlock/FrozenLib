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
