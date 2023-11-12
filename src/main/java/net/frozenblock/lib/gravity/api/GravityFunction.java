package net.frozenblock.lib.gravity.api;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface GravityFunction {
	/***
	 * @param entity The optional entity being tracked
	 * @param y The current y position
	 * @param minY The minimum Y position of the gravity belt
	 * @param maxY The maximum Y position of the gravity belt
	 * @return The gravity value
	 */
	double get(@Nullable Entity entity, double y, double minY, double maxY);
}
