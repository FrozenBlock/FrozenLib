/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.advancement.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.advancements.AdvancementHolder;

public final class AdvancementEvents {
	private AdvancementEvents() {}

	public static final Event<AdvancementInit> INIT = FrozenEvents.createEnvironmentEvent(AdvancementInit.class, callbacks -> context -> {
		for (AdvancementInit callback : callbacks) {
			callback.onInit(context);
		}
	});

	@FunctionalInterface
	public interface AdvancementInit extends CommonEventEntrypoint {
		void onInit(AdvancementHolder holder);
	}
}
