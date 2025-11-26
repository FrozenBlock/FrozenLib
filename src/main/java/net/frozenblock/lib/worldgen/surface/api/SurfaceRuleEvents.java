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

package net.frozenblock.lib.worldgen.surface.api;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Events that allows adding surface rules to dimensions.
 * <p>
 * Defined with the {@code frozenlib:events} key in {@code fabric.mod.json}.
 * <p>
 * Compatible with TerraBlender.
 */
public class SurfaceRuleEvents {

	/**
	 * Lets you modify the Surface Rules of Overworld-based world presets.
	 */
	public static final Event<OverworldSurfaceRuleCallback> MODIFY_OVERWORLD = FrozenEvents.createEnvironmentEvent(OverworldSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) callback.addOverworldSurfaceRules(context);
	});

	/**
	 * Lets you modify the Surface Rules of Overworld-based world presets without checking the preliminary surface.
	 */
	public static final Event<OverworldSurfaceRuleNoPrelimSurfaceCallback> MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE = FrozenEvents.createEnvironmentEvent(OverworldSurfaceRuleNoPrelimSurfaceCallback.class, callbacks -> context -> {
		for (var callback : callbacks) callback.addOverworldNoPrelimSurfaceRules(context);
	});

	/**
	 * Lets you modify the Surface Rules of Nether-based world presets.
	 */
	public static final Event<NetherSurfaceRuleCallback> MODIFY_NETHER = FrozenEvents.createEnvironmentEvent(NetherSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) callback.addNetherSurfaceRules(context);
	});

	/**
	 * Lets you modify the Surface Rules of End-based world presets.
	 */
	public static final Event<EndSurfaceRuleCallback> MODIFY_END = FrozenEvents.createEnvironmentEvent(EndSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) callback.addEndSurfaceRules(context);
	});

	/**
	 * Lets you modify the Surface Rules of custom world presets.
	 */
	public static final Event<GenericSurfaceRuleCallback> MODIFY_GENERIC = FrozenEvents.createEnvironmentEvent(GenericSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) callback.addGenericSurfaceRules(context);
	});

	public interface OverworldSurfaceRuleCallback extends CommonEventEntrypoint {
		void addOverworldSurfaceRules(List<SurfaceRules.RuleSource> context);
	}

	public interface OverworldSurfaceRuleNoPrelimSurfaceCallback extends CommonEventEntrypoint {
		void addOverworldNoPrelimSurfaceRules(List<SurfaceRules.RuleSource> context);
	}

	public interface NetherSurfaceRuleCallback extends CommonEventEntrypoint {
		void addNetherSurfaceRules(List<SurfaceRules.RuleSource> context);
	}

	public interface EndSurfaceRuleCallback extends CommonEventEntrypoint {
		void addEndSurfaceRules(List<SurfaceRules.RuleSource> context);
	}

	public interface GenericSurfaceRuleCallback extends CommonEventEntrypoint {
		void addGenericSurfaceRules(List<FrozenDimensionBoundRuleSource> context);
	}
}
