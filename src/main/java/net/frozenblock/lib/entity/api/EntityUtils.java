/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.entity.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class EntityUtils {
	private static final Map<ServerLevel, List<Entity>> ENTITIES_PER_LEVEL = new Object2ObjectOpenHashMap<>();

	public static void populateEntitiesPerLevel(@NotNull ServerLevel level) {
		clearEntitiesPerLevel(level);
		Iterable<Entity> entityIterable = level.entityManager.getEntityGetter().getAll();
		ArrayList<Entity> entityList = new ArrayList<>();
		entityIterable.forEach(entityList::add);
		ENTITIES_PER_LEVEL.put(level, List.copyOf(entityList));
	}

	public static void clearEntitiesPerLevel(ServerLevel level) {
		ENTITIES_PER_LEVEL.remove(level);
	}

	/**
	 * Returns a {@link List} of entities in the {@link ServerLevel}.
	 *
	 * @param level The {@link ServerLevel} to check for entities in.
	 * @return a {@link List} of entities in the {@link ServerLevel}.
	 */
	public static List<Entity> getEntitiesPerLevel(ServerLevel level) {
		return ENTITIES_PER_LEVEL.computeIfAbsent(level, serverLevel -> new ArrayList<>());
	}

}
