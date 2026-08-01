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

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.DelayedRegistry;
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.FabricDelayedRegistry;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.command.FrozenLibCommand;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.entrypoint.api.FrozenModInitializer;
import net.frozenblock.lib.event.impl.FabricEventBridge;
import net.frozenblock.lib.registry.FrozenLibRegistries;

public final class FrozenLibFabric extends FrozenModInitializer {

	public FrozenLibFabric() {
		super(FrozenLibConstants.MOD_ID);
	}

	@Override
	public void onInitialize(String modId, ModContainer container) {
		DelayedRegistry.setFactory(FabricDelayedRegistry::new);
		FrozenLibMain.preQuiltSetup();
		FrozenLibRegistries.setup();
		FabricEventBridge.initModStage();

		// QUILT INIT
		FrozenLibMain.quiltSetup();

		// CONTINUE FROZENLIB INIT
		FrozenLibMain.setup();

		FrozenMainEntrypoint.EVENT.invoker().init(); // includes dev init

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			FrozenLibCommand.register(dispatcher);
		});
	}
}
