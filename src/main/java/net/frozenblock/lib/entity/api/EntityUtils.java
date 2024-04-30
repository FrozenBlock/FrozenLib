/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
