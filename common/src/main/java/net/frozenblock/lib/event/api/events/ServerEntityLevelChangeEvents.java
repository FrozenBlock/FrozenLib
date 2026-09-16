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

package net.frozenblock.lib.event.api.events;

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

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@UtilityClass
public final class ServerEntityLevelChangeEvents {
	/**
	 * An event which is called after an entity has been moved to a different level.
	 *
	 * <p>All entities are copied to the destination and the old entity removed.
	 * This event does not apply to the {@link ServerPlayer} since players are physically moved to the new level instead of being copied over.
	 *
	 * <p>A mod may use this event for reference cleanup if it is tracking an entity's current level.
	 *
	 * @see ServerEntityLevelChangeEvents#AFTER_PLAYER_CHANGE_LEVEL
	 */
	public static final Event<AfterEntityChange> AFTER_ENTITY_CHANGE_LEVEL = EventRegistry.createEnvironmentEvent(AfterEntityChange.class, callbacks -> (originalEntity, newEntity, origin, destination) -> {
		for (AfterEntityChange callback : callbacks) callback.afterChangeLevel(originalEntity, newEntity, origin, destination);
	});

	/**
	 * An event which is called after a player has been moved to a different level.
	 *
	 * <p>This is similar to {@link #AFTER_ENTITY_CHANGE_LEVEL} but is only called for players.
	 * This is because the player is usually physically moved to the new level instead of being recreated at the destination.
	 *
	 * <p>However, there is one exception to this. When the player respawns in a different level, the player is recreated at the destination. When that happens,
	 * this event passes in the new {@link ServerPlayer}.
	 *
	 * @see #AFTER_ENTITY_CHANGE_LEVEL
	 */
	public static final Event<AfterPlayerChange> AFTER_PLAYER_CHANGE_LEVEL = EventRegistry.createEnvironmentEvent(AfterPlayerChange.class, callbacks -> (player, origin, destination) -> {
		for (AfterPlayerChange callback : callbacks) callback.afterChangeLevel(player, origin, destination);
	});

	@FunctionalInterface
	public interface AfterEntityChange {
		/**
		 * Called after an entity has been recreated at the destination when being moved to a different level.
		 *
		 * <p>Note this event is not called if the entity is a {@link ServerPlayer}.
		 * {@link AfterPlayerChange} should be used to track when a player has changed levels.
		 *
		 * @param originalEntity the original entity
		 * @param newEntity the new entity at the destination
		 * @param origin the level the original entity is in
		 * @param destination the destination level the new entity is in
		 */
		void afterChangeLevel(Entity originalEntity, Entity newEntity, ServerLevel origin, ServerLevel destination);
	}

	@FunctionalInterface
	public interface AfterPlayerChange {
		/**
		 * Called after a player has been moved to different level.
		 *
		 * @param player the player
		 * @param origin the original level the player was in
		 * @param destination the new level the player was moved to
		 */
		void afterChangeLevel(ServerPlayer player, ServerLevel origin, ServerLevel destination);
	}
}
