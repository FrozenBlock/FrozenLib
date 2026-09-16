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

package net.frozenblock.lib.event.api.events.client;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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
 */

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jspecify.annotations.Nullable;

@ClientOnly
public interface ClientTooltipComponentCallback {
	/**
	 * Redirects to {@code ClientTooltipComponentCallback} on Fabric, and is implemented via mixin on NeoForge.
	 */
	Event<ClientTooltipComponentCallback> EVENT = EventRegistry.createEnvironmentEvent(ClientTooltipComponentCallback.class, callbacks -> data -> {
		for (ClientTooltipComponentCallback callback : callbacks) {
			final ClientTooltipComponent component = callback.getClientComponent(data);
			if (component != null) return component;
		}

		return null;
	});

	/**
	 * Return the client tooltip component for the passed tooltip component, or null if none is available.
	 */
	@Nullable
	ClientTooltipComponent getClientComponent(TooltipComponent component);
}
