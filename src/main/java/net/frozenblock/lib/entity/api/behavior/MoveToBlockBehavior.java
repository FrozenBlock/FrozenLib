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

package net.frozenblock.lib.entity.api.behavior;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.lib.entity.impl.behavior.FrozenBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

/**
 * {@link MoveToBlockGoal} as a behavior.
 */
public abstract class MoveToBlockBehavior<E extends PathfinderMob> extends Behavior<E> {
	public static final int DURATION = 1200;
	protected final E mob;
	public final double speedModifier;
	protected int tryTicks;
	protected BlockPos blockPos = BlockPos.ZERO;
	private boolean reachedTarget;
	private final int searchRange;
	private final int verticalSearchRange;
	protected int verticalSearchStart;

	public MoveToBlockBehavior(E mob, double speedModifier, int searchRange) {
		this(mob, speedModifier, searchRange, 1);
	}

	public MoveToBlockBehavior(E mob, double speedModifier, int searchRange, int verticalSearchRange) {
		super(ImmutableMap.of(), DURATION);
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.searchRange = searchRange;
		this.verticalSearchStart = 0;
		this.verticalSearchRange = verticalSearchRange;
	}

	@Override
	public boolean checkExtraStartConditions(ServerLevel level, E owner) {
		return this.findNearestBlock();
	}

	@Override
	public boolean canStillUse(ServerLevel level, E entity, long gameTime) {
		return this.tryTicks >= -((FrozenBehavior) this).getDuration() && this.tryTicks <= DURATION && this.isValidTarget(level, this.blockPos);
	}

	@Override
	public void start(ServerLevel level, E entity, long gameTime) {
		this.moveMobToBlock();
		this.tryTicks = 0;
	}

	protected void moveMobToBlock() {
		this.mob
			.getNavigation()
			.moveTo(this.blockPos.getX() + 0.5, this.blockPos.getY() + 1, this.blockPos.getZ() + 0.5, this.speedModifier);
	}

	public double acceptedDistance() {
		return 1.0;
	}

	protected BlockPos getMoveToTarget() {
		return this.blockPos.above();
	}

	@Override
	protected void tick(ServerLevel level, E owner, long gameTime) {
		BlockPos blockPos = this.getMoveToTarget();
		if (!blockPos.closerToCenterThan(owner.position(), this.acceptedDistance())) {
			this.reachedTarget = false;
			++this.tryTicks;
			if (this.shouldRecalculatePath()) {
				this.mob
						.getNavigation()
						.moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, this.speedModifier);
			}
		} else {
			this.reachedTarget = true;
			--this.tryTicks;
		}
	}

	public boolean shouldRecalculatePath() {
		return this.tryTicks % 40 == 0;
	}

	protected boolean isReachedTarget() {
		return this.reachedTarget;
	}

	/**
	 * Searches and sets new destination block and returns true if a suitable block (specified in {@link #isValidTarget(LevelReader, BlockPos)}) can be found.
	 */
	protected boolean findNearestBlock() {
		BlockPos blockPos = this.mob.blockPosition();
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

		for(int k = this.verticalSearchStart; k <= this.verticalSearchRange; k = k > 0 ? -k : 1 - k) {
			for(int l = 0; l < this.searchRange; ++l) {
				for(int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for(int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						mutableBlockPos.setWithOffset(blockPos, m, k - 1, n);
						if (this.mob.isWithinRestriction(mutableBlockPos) && this.isValidTarget(this.mob.level(), mutableBlockPos)) {
							this.blockPos = mutableBlockPos;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Return true to set given position as destination.
	 */
	public abstract boolean isValidTarget(LevelReader level, BlockPos pos);
}
