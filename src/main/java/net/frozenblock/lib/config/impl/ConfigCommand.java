package net.frozenblock.lib.config.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import java.util.Collection;

public final class ConfigCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("frozenlib_config")
			.then(Commands.literal("reload")
				.then(Commands.argument("modId", StringArgumentType.string())
					.executes(context -> reloadConfigs(StringArgumentType.getString(context, "modId")))
				)
			)
		);
	}

	private static int reloadConfigs(String modId) {
		Collection<Config<?>> configs = ConfigRegistry.getConfigsForMod(modId);
		for (Config<?> config : configs) {
			config.load();
		}

		return 1;
	}
}
