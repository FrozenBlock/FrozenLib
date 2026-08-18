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
import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.DefaultDataComponentsBoundEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@UtilityClass
public class NeoCommonLifecycleEventsBridge {
	private static RegistryAccess pendingRegistries;
	private static boolean pendingClient;

	public static void init() {
		NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.ServerDataLoad.class, event -> {
			pendingRegistries = event.getRegistries();
			pendingClient = false;
		});
		NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.ClientPacketReceived.class, event -> {
			pendingRegistries = event.getRegistries();
			pendingClient = true;
		});
		NeoForge.EVENT_BUS.addListener(DefaultDataComponentsBoundEvent.class, event -> {
			if (pendingRegistries == null) return;
			CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(pendingRegistries, pendingClient);
			pendingRegistries = null;
		});
	}
}
