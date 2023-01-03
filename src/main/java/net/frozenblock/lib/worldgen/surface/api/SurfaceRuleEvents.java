package net.frozenblock.lib.worldgen.surface.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.world.level.levelgen.SurfaceRules;
import java.util.ArrayList;

public class SurfaceRuleEvents {

	/**
	 * Lets you modify the Surface Rules of Overworld-based world presets.
	 * Will not work with TerraBlender.
	 */
	public static final Event<OverworldSurfaceRuleCallback> MODIFY_OVERWORLD = FrozenEvents.createEnvironmentEvent(OverworldSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addRuleSources(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of Nether-based world presets.
	 * Will not work with TerraBlender.
	 */
	public static final Event<NetherSurfaceRuleCallback> MODIFY_NETHER = FrozenEvents.createEnvironmentEvent(NetherSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addRuleSources(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of End-based world presets.
	 * Will not work with TerraBlender.
	 */
	public static final Event<EndSurfaceRuleCallback> MODIFY_END = FrozenEvents.createEnvironmentEvent(EndSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addRuleSources(context);
		}
	});

	/**
	 * Lets you modify the Surface Rules of custom world presets.
	 * Will not work with TerraBlender.
	 */
	public static final Event<GenericSurfaceRuleCallback> MODIFY_GENERIC = FrozenEvents.createEnvironmentEvent(GenericSurfaceRuleCallback.class, callbacks -> context -> {
		for (var callback : callbacks) {
			callback.addRuleSources(context);
		}
	});

	public interface OverworldSurfaceRuleCallback extends CommonEventEntrypoint {
		void addRuleSources(ArrayList<SurfaceRules.RuleSource> context);
	}


	public interface NetherSurfaceRuleCallback extends CommonEventEntrypoint {
		void addRuleSources(ArrayList<SurfaceRules.RuleSource> context);
	}


	public interface EndSurfaceRuleCallback extends CommonEventEntrypoint {
		void addRuleSources(ArrayList<SurfaceRules.RuleSource> context);
	}

	public interface GenericSurfaceRuleCallback extends CommonEventEntrypoint {
		void addRuleSources(ArrayList<FrozenPresetBoundRuleSource> context);
	}
}
