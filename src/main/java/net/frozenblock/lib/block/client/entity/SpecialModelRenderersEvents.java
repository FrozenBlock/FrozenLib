/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.block.client.entity;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;

@Environment(EnvType.CLIENT)
public class SpecialModelRenderersEvents {

	/**
	 * This event is called when {@link net.minecraft.client.renderer.special.SpecialModelRenderers#STATIC_BLOCK_MAPPING} is initialized and calls `put.`
	 */
	public static final Event<OnMapInit> MAP_INIT = FrozenEvents.createEnvironmentEvent(OnMapInit.class, (callbacks) -> (builder) -> {
		for (var callback : callbacks) {
			callback.onMapInit(builder);
		}
	});

	@FunctionalInterface
	public interface OnMapInit extends CommonEventEntrypoint {
		void onMapInit(ImmutableMap.Builder instance);
	}
}
