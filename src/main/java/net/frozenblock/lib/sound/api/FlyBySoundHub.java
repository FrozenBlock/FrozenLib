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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class FlyBySoundHub {

    private static final int PLAY_COOLDOWN = 40;
    private static final int MIN_DISTANCE_FOR_REMOVAL = 16;
    private static final int AUTO_ENTITY_DISTANCE = 3;
    private static final int AUTO_ENTITY_COOLDOWN = 1;

    /**
     * plays sounds automatically when a certain entity is near
     */
    public static final Map<EntityType<?>, FlyBySound> AUTO_ENTITIES_AND_SOUNDS = new Object2ObjectOpenHashMap<>();

    /**
     * the currently playing flyby sounds
    */
    public static final Map<Entity, FlyBySound> FLYBY_ENTITIES_AND_SOUNDS = new Object2ObjectOpenHashMap<>();

    /**
     * cooldowns for playing a sound from the same entity
     */
    public static final Map<Entity, Integer> ENTITY_COOLDOWNS = new Object2ObjectOpenHashMap<>();
    private static int checkAroundCooldown;

    public static void update(Minecraft client, Player player, boolean autoSounds) {
        if (client.level == null) {
            FLYBY_ENTITIES_AND_SOUNDS.clear();
            ENTITY_COOLDOWNS.clear();
            return;
        }
        for (Entity entity : FLYBY_ENTITIES_AND_SOUNDS.keySet()) {
            if (entity != null) {
                Vec3 vel = entity.getDeltaMovement();
                Vec3 playerVel = player.getDeltaMovement();
                Vec3 entityPos = entity.position();
                Vec3 playerPos = player.getEyePosition();
                double distanceTo = entityPos.distanceTo(playerPos);
                double newDistanceTo = entityPos.add(vel).add(vel).distanceTo(playerPos.add(playerVel));

                int cooldown = ENTITY_COOLDOWNS.getOrDefault(entity, 0) - 1;
                ENTITY_COOLDOWNS.put(entity, cooldown);
                if ((distanceTo > newDistanceTo && distanceTo < (vel.lengthSqr() + playerVel.length()) * 2) && cooldown <= 0) {
                    FlyBySound flyBy = FLYBY_ENTITIES_AND_SOUNDS.get(entity);
                    float volume = (float) (flyBy.volume + (vel.length() / 2));
                    client.getSoundManager().play(new EntityBoundSoundInstance(flyBy.sound, flyBy.category, volume, flyBy.pitch, entity, client.level.random.nextLong()));
                    ENTITY_COOLDOWNS.put(entity, PLAY_COOLDOWN);
                }
            }
        }
        //Remove Entities That Aren't Active
        for (Entity entity : FLYBY_ENTITIES_AND_SOUNDS.keySet().stream().toList()) {
            if (entity == null || entity.isRemoved() || entity.isSilent() || (entity.distanceTo(client.getCameraEntity()) > MIN_DISTANCE_FOR_REMOVAL && !AUTO_ENTITIES_AND_SOUNDS.containsKey(entity.getType()))) {
                FLYBY_ENTITIES_AND_SOUNDS.remove(entity);
            }
        }

        if (!AUTO_ENTITIES_AND_SOUNDS.isEmpty()) {
            if (checkAroundCooldown > 0) {
                --checkAroundCooldown;
            } else if (autoSounds) {
                checkAroundCooldown = AUTO_ENTITY_COOLDOWN;
                AABB box = new AABB(player.blockPosition().offset(-AUTO_ENTITY_DISTANCE, -AUTO_ENTITY_DISTANCE, -AUTO_ENTITY_DISTANCE), player.blockPosition().offset(AUTO_ENTITY_DISTANCE, AUTO_ENTITY_DISTANCE, AUTO_ENTITY_DISTANCE));
                for (Entity entity : client.level.getEntities(player, box)) {
                    EntityType<?> type = entity.getType();
                    if (AUTO_ENTITIES_AND_SOUNDS.containsKey(type)) {
                        addEntity(entity, AUTO_ENTITIES_AND_SOUNDS.get(type));
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
