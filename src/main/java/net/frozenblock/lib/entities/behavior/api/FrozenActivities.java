package net.frozenblock.lib.entities.behavior.api;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.schedule.Activity;

public final class FrozenActivities {
	private FrozenActivities() {
		throw new UnsupportedOperationException("FrozenActivities contains only static declarations.");
	}

	public static final Activity TARGET_BLOCK = register("block_target");

	public static void init() {
	}

	private static Activity register(String key) {
		var id = FrozenMain.id(key);
		return Registry.register(Registry.ACTIVITY, id, new Activity(id.toString()));
	}
}
