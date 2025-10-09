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

package net.frozenblock.lib.sound.client.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class FlyBySoundHub {
    private static final int PLAY_COOLDOWN = 40;
    private static final int MIN_DISTANCE_FOR_REMOVAL = 16;
    private static final double AUTO_ENTITY_DISTANCE = 3;
    private static final int AUTO_ENTITY_COOLDOWN = 1;
	private static final int PREDICTION_TICKS = 3;
	private static final double OVERALL_SENSITIVITY = 1.75;
	private static final double HORIZONTAL_SENSITIVITY = 1;
	private static final double VERTICAL_SENSITIVITY = 0.3;
	private static final double BASE_ENTITY_BOUNDING_BOX_EXPANSION = 0.7;
	private static final double BOUNDING_BOX_EXPANSION_PER_VELOCITY = 5;

    /**
     * Plays sounds automatically when a certain entity is near.
     */
    public static final Map<EntityType<?>, FlyBySound> AUTO_ENTITIES_AND_SOUNDS = new Object2ObjectOpenHashMap<>();

    /**
     * The currently playing flyby sounds.
    */
    public static final Map<Entity, FlyBySound> FLYBY_ENTITIES_AND_SOUNDS = new Object2ObjectOpenHashMap<>();

    /**
     * Cooldowns for playing a sound from the same entity.
     */
    public static final Map<Entity, Integer> ENTITY_COOLDOWNS = new Object2ObjectOpenHashMap<>();

    public static void tick(@NotNull Minecraft client, Entity cameraEntity, boolean autoSounds) {
		if (client.level == null || cameraEntity == null || !client.level.tickRateManager().runsNormally()) {
			FLYBY_ENTITIES_AND_SOUNDS.clear();
			return;
		}

		final Vec3 cameraPos = cameraEntity.getEyePosition();
		final double cameraEntityWidth = cameraEntity.getBbWidth();
		final double detectionSize = cameraEntityWidth * 2D;
		final AABB playerHeadBox = new AABB(
			cameraEntity.getEyePosition().add(-detectionSize, -detectionSize, -detectionSize),
			cameraEntity.getEyePosition().add(detectionSize, detectionSize, detectionSize)
		);

		for (Entity entity : FLYBY_ENTITIES_AND_SOUNDS.keySet()) {
			if (entity != null) {
				Vec3 entityVelocity = (entity.getPosition(1F).subtract(entity.getPosition(0F))).scale(OVERALL_SENSITIVITY);
				entityVelocity = entityVelocity.multiply(HORIZONTAL_SENSITIVITY, VERTICAL_SENSITIVITY, HORIZONTAL_SENSITIVITY);
				final double entityVelocityLength = entityVelocity.length();
				final AABB entityBox = entity.getBoundingBox().inflate(BASE_ENTITY_BOUNDING_BOX_EXPANSION + (entityVelocityLength * BOUNDING_BOX_EXPANSION_PER_VELOCITY));

				if (playerHeadBox.intersects(entityBox)) {
					final Vec3 entityPos = entity.getPosition(1F);
					final int cooldown = ENTITY_COOLDOWNS.getOrDefault(entity, 0) - 1;
					ENTITY_COOLDOWNS.put(entity, cooldown);
					final Vec3 movedPos = entityPos.add(entityVelocity.scale(PREDICTION_TICKS));

					if (hasPassed(cameraPos, cameraEntityWidth, entityPos, movedPos) && cooldown <= 0) {
						final 	double deltaDistance = Math.abs(entityPos.distanceTo(cameraPos) - movedPos.distanceTo(cameraPos));
						final FlyBySound flyBy = FLYBY_ENTITIES_AND_SOUNDS.get(entity);
						final float volume = (float) (flyBy.volume + (deltaDistance));
						client.getSoundManager().play(new EntityBoundSoundInstance(flyBy.sound, flyBy.category, volume, flyBy.pitch, entity, client.level.random.nextLong()));
						ENTITY_COOLDOWNS.put(entity, PLAY_COOLDOWN);
					}
				}
			}
		}

		//Remove entities that aren't active
		for (Entity entity : FLYBY_ENTITIES_AND_SOUNDS.keySet().stream().toList()) {
			if (entity == null || entity.isRemoved() || entity.isSilent() || (cameraPos.distanceTo(entity.position()) > MIN_DISTANCE_FOR_REMOVAL)) {
				FLYBY_ENTITIES_AND_SOUNDS.remove(entity);
			}
		}

		//Check for entities in the auto flyby list
		if (!AUTO_ENTITIES_AND_SOUNDS.isEmpty()) {
			//if (checkAroundCooldown > 0) {
			//	--checkAroundCooldown;
			//} else
			if (autoSounds) {
				//checkAroundCooldown = AUTO_ENTITY_COOLDOWN;
				final AABB box = new AABB(
					cameraPos.add(-AUTO_ENTITY_DISTANCE, -AUTO_ENTITY_DISTANCE, -AUTO_ENTITY_DISTANCE),
					cameraPos.add(AUTO_ENTITY_DISTANCE, AUTO_ENTITY_DISTANCE, AUTO_ENTITY_DISTANCE)
				);
				for (Entity entity : client.level.getEntities(cameraEntity, box)) {
					final EntityType<?> type = entity.getType();
					if (AUTO_ENTITIES_AND_SOUNDS.containsKey(type)) addEntity(entity, AUTO_ENTITIES_AND_SOUNDS.get(type));
				}
			}
		}
	}

	public static boolean hasPassed(@NotNull Vec3 cameraPos, double cameraWidth, @NotNull Vec3 oldCoord, @NotNull Vec3 newCoord) {
		return hasPassedCoordinate(cameraPos.x(), cameraWidth, 0.35D, oldCoord.x(), newCoord.x()) ||
			hasPassedCoordinate(cameraPos.z(), cameraWidth, 0.35D, oldCoord.z(), newCoord.z()) ||
			hasPassedCoordinate(cameraPos.y(), cameraWidth, 0.25D, oldCoord.y(), newCoord.y());
	}

	public static boolean hasPassedCoordinate(double cameraCoord, double cameraWidth, double triggerWidth, double oldCoord, double newCoord) {
		double cameraTriggerWidth = cameraWidth * triggerWidth;
		double minCamera = cameraCoord - cameraWidth;
		double minCameraTrigger = cameraCoord - cameraTriggerWidth;
		double maxCamera = cameraCoord + cameraWidth;
		double maxCameraTrigger = cameraCoord + cameraTriggerWidth;
		if (oldCoord < minCamera) {
			return newCoord > minCameraTrigger;
		} else if (oldCoord > maxCamera) {
			return newCoord < maxCameraTrigger;
		}
		return false;
	}

    public static void addEntity(Entity entity, FlyBySound flyBySound) {
        FLYBY_ENTITIES_AND_SOUNDS.put(entity, flyBySound);
    }

    public static void addEntityType(EntityType<?> type, FlyBySound flyBySound) {
        AUTO_ENTITIES_AND_SOUNDS.put(type, flyBySound);
    }

    public record FlyBySound(float pitch, float volume, SoundSource category, SoundEvent sound) {
    }
}
