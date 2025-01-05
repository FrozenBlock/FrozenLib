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
