/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.material.api;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Events that allows adding surface rules to dimensions.
 * <p>
 * Defined with the {@code frozenlib:events} key in {@code fabric.mod.json}.
 * <p>
 * Compatible with TerraBlender.
 */
public class MaterialRuleEvents {

	/**
	 * Lets you modify the Material Rules of Overworld-based world presets.
	 */
	public static final Event<OverworldMaterialRuleCallback> MODIFY_OVERWORLD = FrozenEvents.createEnvironmentEvent(OverworldMaterialRuleCallback.class, callbacks -> (registryAccess, context) -> {
		for (var callback : callbacks) callback.addOverworldMaterialRules(registryAccess, context);
	});

	/**
	 * Lets you modify the Material Rules of Overworld-based world presets without checking the preliminary surface.
	 */
	public static final Event<OverworldMaterialRuleNoPrelimSurfaceCallback> MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE = FrozenEvents.createEnvironmentEvent(OverworldMaterialRuleNoPrelimSurfaceCallback.class, callbacks -> (registryAccess, context) -> {
		for (var callback : callbacks) callback.addOverworldNoPrelimMaterialRules(registryAccess, context);
	});

	/**
	 * Lets you modify the Surface Rules of Nether-based world presets.
	 */
	public static final Event<NetherMaterialRuleCallback> MODIFY_NETHER = FrozenEvents.createEnvironmentEvent(NetherMaterialRuleCallback.class, callbacks -> (registryAccess, context) -> {
		for (var callback : callbacks) callback.addNetherMaterialRules(registryAccess, context);
	});

	/**
	 * Lets you modify the Material Rules of End-based world presets.
	 */
	public static final Event<EndMaterialRuleCallback> MODIFY_END = FrozenEvents.createEnvironmentEvent(EndMaterialRuleCallback.class, callbacks -> (registryAccess, context) -> {
		for (var callback : callbacks) callback.addEndMaterialRules(registryAccess, context);
	});

	/**
	 * Lets you modify the Material Rules of custom world presets.
	 */
	public static final Event<GenericMaterialRuleCallback> MODIFY_GENERIC = FrozenEvents.createEnvironmentEvent(GenericMaterialRuleCallback.class, callbacks -> (registryAccess, context) -> {
		for (var callback : callbacks) callback.addGenericMaterialRules(registryAccess, context);
	});

	public interface OverworldMaterialRuleCallback extends CommonEventEntrypoint {
		void addOverworldMaterialRules(RegistryAccess registryAccess, List<SurfaceRules.RuleSource> context);
	}

	public interface OverworldMaterialRuleNoPrelimSurfaceCallback extends CommonEventEntrypoint {
		void addOverworldNoPrelimMaterialRules(RegistryAccess registryAccess, List<SurfaceRules.RuleSource> context);
	}

	public interface NetherMaterialRuleCallback extends CommonEventEntrypoint {
		void addNetherMaterialRules(RegistryAccess registryAccess, List<SurfaceRules.RuleSource> context);
	}

	public interface EndMaterialRuleCallback extends CommonEventEntrypoint {
		void addEndMaterialRules(RegistryAccess registryAccess, List<SurfaceRules.RuleSource> context);
	}

	public interface GenericMaterialRuleCallback extends CommonEventEntrypoint {
		void addGenericMaterialRules(RegistryAccess registryAccess, List<DimensionBoundRuleSource> context);
	}
}
