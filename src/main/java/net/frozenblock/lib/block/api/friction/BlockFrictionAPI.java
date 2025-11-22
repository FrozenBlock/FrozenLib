/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.api.friction;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;

public class BlockFrictionAPI {

	public static final Event<FrictionModification> MODIFICATIONS = FrozenEvents.createEnvironmentEvent(
		FrictionModification.class,
		callbacks -> context -> {
			for (FrictionModification modification : callbacks) modification.modifyFriction(context);
		});

	@FunctionalInterface
	public interface FrictionModification extends CommonEventEntrypoint {
		void modifyFriction(FrictionContext context);
	}
}
