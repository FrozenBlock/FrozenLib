package net.frozenblock.lib.command.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.frozenblock.lib.config.impl.client.ClientConfigCommand;

@Environment(EnvType.CLIENT)
public final class FrozenLibClientCommand {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(
			ClientCommands.literal("frozenlib_client")
				.then(ClientConfigCommand.buildSubCommand())
		);
	}
}
