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

package net.frozenblock.lib.renderer.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.event.api.FrozenEvent;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.renderer.block.BuiltInBlockModels;

/**
 * Helps with registering a built-in block model.
 */
@Environment(EnvType.CLIENT)
public class BuiltInBlockModelRegistry {
	/**
	 * The event that is triggered when built-in block models are being created.
	 */
	public static final FrozenEvent<Register> REGISTER = FrozenEvents.createEnvironmentEvent(Register.class, (callbacks) -> (builder) -> {
		for (var callback : callbacks) callback.addBuiltInBlockModels(builder);
	});

	/**
	 * A functional interface representing a register event.
	 */
	@FunctionalInterface
	public interface Register extends ClientEventEntrypoint {
		/**
		 * Triggers the event when built-in block models are being created.
		 * @param builder the {@link net.minecraft.client.renderer.block.BuiltInBlockModels.Builder} to add block models to.
		 */
		void addBuiltInBlockModels(BuiltInBlockModels.Builder builder);
	}
}
