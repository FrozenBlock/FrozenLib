/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.frozenblock.lib.command.client.FrozenLibClientCommand;
import net.frozenblock.lib.entrypoint.api.FrozenClientEntrypoint;
import net.frozenblock.lib.event.impl.FabricEventBridge;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.networking.impl.FrozenLibClientNetworking;
import net.minecraft.client.Minecraft;

public final class FrozenLibClientFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FabricEventBridge.initClientModStage();

		FrozenLibClient.preQuiltInit();
		ModIntegrations.initializePreFreeze(); // Mod integrations must run after normal mod initialization

		// QUILT INIT
		FrozenLibClient.quiltSetup();

		// CONTINUE FROZENLIB INIT
		FrozenLibClient.init();
		FrozenLibClientNetworking.registerClientReceivers();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
			FrozenLibClientCommand.register(
				(CommandDispatcher) dispatcher,
				string -> (LiteralArgumentBuilder) ClientCommands.literal(string),
				(string, type) -> (RequiredArgumentBuilder) ClientCommands.argument(string, type),
				message -> {
					final Minecraft minecraft = Minecraft.getInstance();
					minecraft.gui.hud.getChat().addClientSystemMessage(message);
					minecraft.getNarrator().saySystemChatQueued(message);
				}
			);
		});

		FrozenClientEntrypoint.EVENT.invoker().init(); // also includes dev init
	}
}
