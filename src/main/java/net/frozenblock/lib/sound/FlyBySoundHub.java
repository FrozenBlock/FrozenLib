package net.frozenblock.lib.sound;

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

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class FlyBySoundHub {

    public static Map<EntityType<?>, FlyBySound> autoEntitiesAndSounds =
            new HashMap<>();

    public static Map<Entity, FlyBySound> flybyEntitiesAndSounds =
            new HashMap<>();
    public static Map<Entity, Integer> entityCooldowns = new HashMap<>();
    private static int checkAroundCooldown;

    public static void update(Minecraft client, Player player,
                              boolean autoSounds) {
        for (Entity entity : flybyEntitiesAndSounds.keySet()) {
            if (client.level == null) {
                flybyEntitiesAndSounds.clear();
                return;
            }
            if (entity != null) {
                Vec3 vel = entity.getDeltaMovement();
                Vec3 playerVel = player.getDeltaMovement();
                Vec3 entityPos = entity.position();
                Vec3 playerPos = player.getEyePosition();
                double distanceTo = entityPos.distanceTo(playerPos);
                double newDistanceTo = entityPos.add(vel).add(vel)
                        .distanceTo(playerPos.add(playerVel));

                int cooldown = entityCooldowns.getOrDefault(entity, 0) - 1;
                entityCooldowns.put(entity, cooldown);
                if ((distanceTo > newDistanceTo && distanceTo <
                        (vel.lengthSqr() + playerVel.length()) * 2) &&
                        cooldown <= 0) {
                    FlyBySound flyBy = flybyEntitiesAndSounds.get(entity);
                    float volume = (float) (flyBy.volume + (vel.length() / 2));
                    client.getSoundManager()
                            .play(new EntityBoundSoundInstance(flyBy.sound,
                                    flyBy.category, volume, flyBy.pitch, entity,
                                    client.level.random.nextLong()));
                    entityCooldowns.put(entity, 40);
                }
            }
        }
        //Remove Entities That Aren't Active
        for (Entity entity : flybyEntitiesAndSounds.keySet().stream()
                .toList()) {
            if (entity == null || entity.isRemoved() || entity.isSilent() ||
                    (entity.distanceTo(client.getCameraEntity()) > 16 &&
                            !autoEntitiesAndSounds.containsKey(
                                    entity.getType()))) {
                flybyEntitiesAndSounds.remove(entity);
            }
        }

        if (!autoEntitiesAndSounds.isEmpty()) {
            if (checkAroundCooldown > 0) {
                --checkAroundCooldown;
            } else {
                if (client.level != null && autoSounds) {
                    checkAroundCooldown = 1;
                    AABB box =
                            new AABB(player.blockPosition().offset(-3, -3, -3),
                                    player.blockPosition().offset(3, 3, 3));
                    for (Entity entity : client.level.getEntities(player,
                            box)) {
                        EntityType<?> type = entity.getType();
                        if (autoEntitiesAndSounds.containsKey(type)) {
                            addEntity(entity, autoEntitiesAndSounds.get(type));
                        }
                    }
                }
            }
        }
    }

    public static void addEntity(Entity entity, FlyBySound flyBySound) {
        flybyEntitiesAndSounds.put(entity, flyBySound);
    }

    public record FlyBySound(float pitch, float volume, SoundSource category,
                             SoundEvent sound) {
    }
}
