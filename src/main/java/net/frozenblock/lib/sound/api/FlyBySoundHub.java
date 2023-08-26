/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.sound.api;

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

@Environment(EnvType.CLIENT)
public final class FlyBySoundHub {

    private static final int PLAY_COOLDOWN = 40;
    private static final int MIN_DISTANCE_FOR_REMOVAL = 16;
    private static final int AUTO_ENTITY_DISTANCE = 3;
    private static final int AUTO_ENTITY_COOLDOWN = 1;

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
	private static int checkAroundCooldown;

    public static void update(Minecraft client, Entity cameraEntity, boolean autoSounds) {
		if (client.level != null && cameraEntity != null) {
			Vec3 playerPos = cameraEntity.getEyePosition();
			double playerBBWidth = cameraEntity.getBbWidth() * 2;
			AABB playerHeadBox = new AABB(cameraEntity.getEyePosition().add(-playerBBWidth, -playerBBWidth, -playerBBWidth), cameraEntity.getEyePosition().add(playerBBWidth, playerBBWidth, playerBBWidth));

			for (Entity entity : FLYBY_ENTITIES_AND_SOUNDS.keySet()) {
				//Stop if level isn't loaded
				if (client.level == null) {
					FLYBY_ENTITIES_AND_SOUNDS.clear();
					return;
				}

				if (entity != null) {
					Vec3 entityVelocity = entity.getPosition(1F).subtract(entity.getPosition(0F));
					entityVelocity = entityVelocity.multiply(1.2, 0.6, 1.2);
					double entityVelocityLength = entityVelocity.length();
					AABB entityBox = entity.getBoundingBox().inflate(0.7D + (entityVelocityLength * 5));

					if (playerHeadBox.intersects(entityBox)) {
						if (entityVelocityLength > 0.05) {
							Vec3 entityPos = entity.position();
							double distanceTo = entityPos.distanceTo(playerPos);
							double newDistanceTo = entityPos.add(entityVelocity.scale(2)).distanceTo(playerPos);
							int cooldown = ENTITY_COOLDOWNS.getOrDefault(entity, 0) - 1;
							ENTITY_COOLDOWNS.put(entity, cooldown);
							if (distanceTo > newDistanceTo && newDistanceTo < entityVelocityLength * 2 && cooldown <= 0) {
								double deltaDistance = distanceTo - newDistanceTo;
								if (deltaDistance > 0.05) {
									FlyBySound flyBy = FLYBY_ENTITIES_AND_SOUNDS.get(entity);
									float volume = (float) (flyBy.volume + (deltaDistance));
									client.getSoundManager().play(new EntityBoundSoundInstance(flyBy.sound, flyBy.category, volume, flyBy.pitch, entity, client.level.random.nextLong()));
									ENTITY_COOLDOWNS.put(entity, PLAY_COOLDOWN);
								}
							}
						}
					}
				}
			}

			//Remove entities that aren't active
			for (Entity entity : FLYBY_ENTITIES_AND_SOUNDS.keySet().stream().toList()) {
				if (entity == null || entity.isRemoved() || entity.isSilent() || (entity.distanceTo(entity) > MIN_DISTANCE_FOR_REMOVAL)) {
					FLYBY_ENTITIES_AND_SOUNDS.remove(entity);
				}
			}

			//Check for entities in the auto flyby list
			if (!AUTO_ENTITIES_AND_SOUNDS.isEmpty()) {
				if (checkAroundCooldown > 0) {
					--checkAroundCooldown;
				} else if (autoSounds) {
					checkAroundCooldown = AUTO_ENTITY_COOLDOWN;
					AABB box = new AABB(cameraEntity.blockPosition().offset(-AUTO_ENTITY_DISTANCE, -AUTO_ENTITY_DISTANCE, -AUTO_ENTITY_DISTANCE), cameraEntity.blockPosition().offset(AUTO_ENTITY_DISTANCE, AUTO_ENTITY_DISTANCE, AUTO_ENTITY_DISTANCE));
					for (Entity entity : client.level.getEntities(cameraEntity, box)) {
						EntityType<?> type = entity.getType();
						if (AUTO_ENTITIES_AND_SOUNDS.containsKey(type)) {
							addEntity(entity, AUTO_ENTITIES_AND_SOUNDS.get(type));
						}
					}
				}
			}
		}
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
