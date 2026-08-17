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

package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

@UtilityClass
public class FabricPlayerBlockBreakEventsBridge {
	public static void init() {
		PlayerBlockBreakEvents.BEFORE.register(((level, player, pos, state, blockEntity) ->
			net.frozenblock.lib.event.api.events.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, blockEntity))
		);

		PlayerBlockBreakEvents.AFTER.register(((level, player, pos, state, blockEntity) ->
			net.frozenblock.lib.event.api.events.PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, player, pos, state, blockEntity))
		);

		PlayerBlockBreakEvents.CANCELED.register(((level, player, pos, state, blockEntity) ->
			net.frozenblock.lib.event.api.events.PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, player, pos, state, blockEntity))
		);
	}
}
