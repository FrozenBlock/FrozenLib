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

package net.frozenblock.lib.entity.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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

	public static List<Entity> getEntitiesPerLevel(ServerLevel level) {
		return ENTITIES_PER_LEVEL.computeIfAbsent(level, serverLevel -> new ArrayList<>());
	}

	public static Optional<Direction> getMovementDirectionHorizontal(@NotNull Entity entity) {
		Direction direction = null;
		Vec3 deltaMovement = entity.getDeltaMovement();
		if (deltaMovement.horizontalDistance() > 0) {
			double nonNegX = Math.abs(deltaMovement.x);
			double nonNegZ = Math.abs(deltaMovement.z);
			if (nonNegX > nonNegZ) {
				direction = deltaMovement.x > 0 ? Direction.EAST : Direction.WEST;
			} else if (nonNegZ > 0) {
				direction = deltaMovement.z > 0 ? Direction.SOUTH : Direction.NORTH;
			}
		}
		return Optional.ofNullable(direction);
	}

}
