/*
 * Copyright (c) 2024 FabricMC
 * Copyright (c) 2024 FrozenBlock
 * Modified to use Mojang's Official Mappings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

package net.fabricmc.frozenblock.datafixer.impl.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;

public final class ServerFreezer implements DedicatedServerModInitializer {
	// From QSL.
	@Override
	public void onInitializeServer() {
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> FabricDataFixesInternals.get().freeze());
	}
}
