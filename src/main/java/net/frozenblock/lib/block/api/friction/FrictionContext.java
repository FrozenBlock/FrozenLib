package net.frozenblock.lib.block.api.friction;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FrictionContext {

	/**
	 * A mutable property that will determine the outputting friction
	 */
	public float friction;

	public final Level level;
	public final LivingEntity entity;
	public final BlockState state;

	public FrictionContext(Level level, LivingEntity entity, BlockState state, float friction) {
		this.level = level;
		this.entity = entity;
		this.state = state;

		this.friction = friction;
	}
}
