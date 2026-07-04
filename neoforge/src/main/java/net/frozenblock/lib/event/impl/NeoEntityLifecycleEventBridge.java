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
import net.frozenblock.lib.event.api.events.ClientEntityLifecycleEvents;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@UtilityClass
public class NeoEntityLifecycleEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class, NeoEntityLifecycleEventBridge::onJoin);
		NeoForge.EVENT_BUS.addListener(EntityLeaveLevelEvent.class, NeoEntityLifecycleEventBridge::onLeave);
	}

	private static void onJoin(EntityJoinLevelEvent event) {
		final Level level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			EntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(event.getEntity(), serverLevel);
		} else if (level instanceof ClientLevel clientLevel) {
			ClientEntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(event.getEntity(), clientLevel);
		}
	}

	private static void onLeave(EntityLeaveLevelEvent event) {
		final Level level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			EntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(event.getEntity(), serverLevel);
		} else if (level instanceof ClientLevel clientLevel) {
			ClientEntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(event.getEntity(), clientLevel);
		}
	}
}
