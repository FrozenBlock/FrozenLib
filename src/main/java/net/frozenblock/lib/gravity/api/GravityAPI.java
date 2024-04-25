/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.gravity.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class GravityAPI {
    private GravityAPI() {}

    public static final Vec3 DEFAULT_GRAVITY = new Vec3(0.0, 1.0, 0.0);

    public static final Event<GravityModification> MODIFICATIONS = FrozenEvents.createEnvironmentEvent(GravityModification.class, callbacks -> context -> {
        for (GravityModification callback : callbacks) {
            callback.modifyGravity(context);
        }
    });

    private static final Map<ResourceKey<Level>, List<GravityBelt<?>>> GRAVITY_BELTS = new HashMap<>();

    public static void register(ResourceKey<Level> dimension, GravityBelt<?> gravityBelt) {
		getAllBelts(dimension).add(gravityBelt);
    }

	@NotNull
    public static List<GravityBelt<?>> getAllBelts(ResourceKey<Level> dimension) {
        return GRAVITY_BELTS.computeIfAbsent(dimension, dimension1 -> new ArrayList<>());
    }

    public static List<GravityBelt<?>> getAllBelts(Level level) {
        return getAllBelts(level.dimension());
    }

    static {
        MODIFICATIONS.register(context -> {
            if (GRAVITY_BELTS.containsKey(context.dimension)) {
                Optional<GravityBelt<?>> optionalGravityBelt = getAffectingGravityBelt(GRAVITY_BELTS.get(context.dimension), context.y);
                if (optionalGravityBelt.isPresent()) {
                    GravityBelt<?> belt = optionalGravityBelt.get();
                    context.gravity = belt.getGravity(null, context.y);
                }
            }
        });
    }

    public static Vec3 calculateGravity(ResourceKey<Level> dimension, double y) {
        GravityContext context = new GravityContext(dimension, y, null);
        MODIFICATIONS.invoker().modifyGravity(context);
        return context.gravity;
    }

    public static Vec3 calculateGravity(Level level, double y) {
        return calculateGravity(level.dimension(), y);
    }

    public static Vec3 calculateGravity(Entity entity) {
        ResourceKey<Level> dimension = entity.level().dimension();
        double y = entity.getY();
        GravityContext context = new GravityContext(dimension, y, entity);
        MODIFICATIONS.invoker().modifyGravity(context);
        return context.gravity;
    }

    public static Optional<GravityBelt<?>> getAffectingGravityBelt(List<GravityBelt<?>> belts, double y) {
        Optional<GravityBelt<?>> optionalGravityBelt = Optional.empty();
        for (GravityBelt<?> belt : belts) {
            if (belt.affectsPosition(y)) {
                optionalGravityBelt = Optional.of(belt);
                break;
            }
        }
        return optionalGravityBelt;
    }

    @FunctionalInterface
    public interface GravityModification extends CommonEventEntrypoint {
        void modifyGravity(GravityContext context);
    }
}
