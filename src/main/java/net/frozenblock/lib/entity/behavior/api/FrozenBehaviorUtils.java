package net.frozenblock.lib.entity.behavior.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.OneShot;

public final class FrozenBehaviorUtils {
	private FrozenBehaviorUtils() {
		throw new UnsupportedOperationException("FrozenBehaviorUtils contains only static declarations.");
	}

	public static <E extends LivingEntity> OneShot<E> getOneShot(BehaviorControl<E> control) {
		if (!(control instanceof OneShot<E> oneShot)) {
			throw new IllegalStateException("Behavior control is not a OneShot");
		} else {
			return oneShot;
		}
	}
}
