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
		for (var callback : callbacks) {
			callback.addOverworldSurfaceRules(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of Overworld-based world presets without checking the preliminary surface.
	 */
	public static final Event<OverworldSurfaceRuleNoPrelimSurfaceCallback> MODIFY_OVERWORLD_NO_PRELIMINARY_SURFACE = FrozenEvents.createEnvironmentEvent(OverworldSurfaceRuleNoPrelimSurfaceCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addOverworldNoPrelimSurfaceRules(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of Nether-based world presets.
	 */
	public static final Event<NetherSurfaceRuleCallback> MODIFY_NETHER = FrozenEvents.createEnvironmentEvent(NetherSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addNetherSurfaceRules(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of End-based world presets.
	 */
	public static final Event<EndSurfaceRuleCallback> MODIFY_END = FrozenEvents.createEnvironmentEvent(EndSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addEndSurfaceRules(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of custom world presets.
	 */
	public static final Event<GenericSurfaceRuleCallback> MODIFY_GENERIC = FrozenEvents.createEnvironmentEvent(GenericSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addGenericSurfaceRules(context);
		}
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
