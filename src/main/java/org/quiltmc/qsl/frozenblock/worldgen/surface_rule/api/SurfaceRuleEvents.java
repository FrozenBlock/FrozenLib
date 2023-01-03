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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Events relating to {@link net.minecraft.world.level.levelgen.SurfaceRules surface rules}.
 * <p>
 * <b>Modification events</b> like {@link #MODIFY_OVERWORLD}, {@link #MODIFY_NETHER}, and {@link #MODIFY_THE_END} allows to modify the surface rules
 * for the related Vanilla dimensions.
 * <p>
 * Modified to work on Fabric
 */
public final class SurfaceRuleEvents {
    /**
     * Represents the event phase named {@code quilt:remove} for the modification events for which removals may happen.
     * <p>
     * This phase always happen after the {@link Event#DEFAULT_PHASE default phase}.
     */
    public static final ResourceLocation REMOVE_PHASE = new ResourceLocation("frozenblock_quilt", "remove");

    /**
     * An event indicating that the surface rules for the Overworld dimension may get modified by mods, allowing the injection of modded surface rules.
     */
    public static final Event<OverworldModifierCallback> MODIFY_OVERWORLD = FrozenEvents.createEnvironmentEvent(OverworldModifierCallback.class, callbacks -> context -> {
        for (var callback : callbacks) {
            callback.modifyOverworldRules(context);
        }
    });

    /**
     * An event indicating that the surface rules for the Nether dimension may get modified by mods, allowing the injection of modded surface rules.
     */
    public static final Event<NetherModifierCallback> MODIFY_NETHER = FrozenEvents.createEnvironmentEvent(NetherModifierCallback.class, callbacks -> context -> {
        for (var callback : callbacks) {
            callback.modifyNetherRules(context);
        }
    });

    /**
     * An event indicating that the surface rules for the End dimension may get modified by mods, allowing the injection of modded surface rules.
     */
    public static final Event<TheEndModifierCallback> MODIFY_THE_END = FrozenEvents.createEnvironmentEvent(TheEndModifierCallback.class, callbacks -> context -> {
        for (var callback : callbacks) {
            callback.modifyTheEndRules(context);
        }
    });

    @FunctionalInterface
    public interface OverworldModifierCallback extends CommonEventEntrypoint {
        /**
         * Called to modify the given Overworld surface rules.
         *
         * @param context the modification context
         */
        void modifyOverworldRules(@NotNull SurfaceRuleContext.Overworld context);
    }

    @FunctionalInterface
    public interface NetherModifierCallback extends CommonEventEntrypoint {
        /**
         * Called to modify the given Nether surface rules.
         *
         * @param context the modification context
         */
        void modifyNetherRules(@NotNull SurfaceRuleContext.Nether context);
    }

    @FunctionalInterface
    public interface TheEndModifierCallback extends CommonEventEntrypoint {
        /**
         * Called to modify the given End surface rules.
         *
         * @param context the modification context
         */
        void modifyTheEndRules(@NotNull SurfaceRuleContext.TheEnd context);
    }

    private SurfaceRuleEvents() {
        throw new UnsupportedOperationException("SurfaceMaterialRuleEvents contains only static definitions.");
    }

    static {
        MODIFY_OVERWORLD.addPhaseOrdering(Event.DEFAULT_PHASE, REMOVE_PHASE);
        MODIFY_NETHER.addPhaseOrdering(Event.DEFAULT_PHASE, REMOVE_PHASE);
        MODIFY_THE_END.addPhaseOrdering(Event.DEFAULT_PHASE, REMOVE_PHASE);
    }
}
