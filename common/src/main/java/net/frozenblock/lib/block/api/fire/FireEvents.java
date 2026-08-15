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

package net.frozenblock.lib.block.api.fire;

import java.util.Optional;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class FireEvents {
	/**
	 * The event that is triggered when an {@link Entity} is catching on fire and the {@link FireType} is being selected.
	 */
	public static final Event<SelectFireType> SELECT_FIRE_TYPE = EventRegistry.createEnvironmentEvent(
		SelectFireType.class,
		(callbacks) -> (entity, sourceBlock, sourceEntity, sourceItem, useMobEffects) -> {
			ResourceKey<FireType> type = FireTypes.DEFAULT;

			if (sourceBlock.isPresent()) {
				final Optional<ResourceKey<FireType>> blockBasedType = FireTypes.getTypeKeyForBlock(entity.registryAccess(), sourceBlock.get(), false);
				if (blockBasedType.isPresent()) type = blockBasedType.get();
			}

			if (sourceEntity.isPresent()) {
				final Optional<ResourceKey<FireType>> sourceEntityBasedType = FireTypes.getTypeFromEntity(sourceEntity.get());
				if (sourceEntityBasedType.isPresent()) type = sourceEntityBasedType.get();
			}

			final Optional<ResourceKey<FireType>> entityBasedType = FireTypes.getTypeKeyForEntity(entity, useMobEffects);
			if (entityBasedType.isPresent()) type = entityBasedType.get();

			for (var callback : callbacks) {
				final ResourceKey<FireType> eventType = callback.selectFireType(entity, sourceBlock, sourceEntity, sourceItem, useMobEffects);
				if (eventType != null) type = eventType;
			}

			return type;
		}
	);

	/**
	 * The event that is triggered after an {@link Entity} is caught on fire and the {@link FireType} is set.
	 * <p>
	 * Runs after {@link FireEvents#SELECT_FIRE_TYPE}.
	 */
	public static final Event<EntityFireTypeSet> AFTER_FIRE_TYPE_SET = EventRegistry.createEnvironmentEvent(
		EntityFireTypeSet.class,
		(callbacks) -> (entity, fireType) -> {
			for (var callback : callbacks) callback.onEntityFireTypeSet(entity, fireType);
		}
	);

	/**
	 * The event that is triggered when an {@link Entity} is burnt from fire lingering on them.
	 */
	public static final Event<EntityBurnTick> ON_ENTITY_BURN_TICK = EventRegistry.createEnvironmentEvent(
		EntityBurnTick.class,
		(callbacks) -> (entity, fireType) -> {
			for (var callback : callbacks) callback.onEntityBurnTick(entity, fireType);
		}
	);

	/**
	 * The event that is triggered when {@link BaseFireBlock#getState(BlockGetter, BlockPos)} is called.
	 * <p>
	 * This event is used to alter the {@link BlockState} that gets placed (i.e., Soul and Copper Fire.)
	 */
	public static final Event<SelectFireBlockState> SELECT_FIRE_BLOCK_STATE = EventRegistry.createEnvironmentEvent(
		SelectFireBlockState.class,
		(callbacks) -> (level, belowPos, belowState) -> {
			BlockState newState = null;
			for (var callback : callbacks) newState = callback.selectFireBlockState(level, belowPos, belowState);
			return newState;
		}
	);

	/**
	 * A functional interface representing a select fire type event.
	 */
	@FunctionalInterface
	public interface SelectFireType extends CommonEventEntrypoint {
		/**
		 * Runs when an {@link Entity} is catching on fire and the {@link FireType} is being selected.
		 * @param entity the {@link Entity} being set on fire
		 * @param sourceBlock the source {@link Block} of the fire, if available
		 * @param sourceEntity the source {@link Entity} of the fire, if available
		 * @param sourceItem the source {@link ItemStack} of the fire, if available
		 * @param useMobEffects whether {@link MobEffect}s should be taken into account
		 */
		ResourceKey<FireType> selectFireType(Entity entity, Optional<Block> sourceBlock, Optional<Entity> sourceEntity, Optional<ItemStack> sourceItem, boolean useMobEffects);
	}

	/**
	 * A functional interface representing an entity fire type set event.
	 */
	@FunctionalInterface
	public interface EntityFireTypeSet extends CommonEventEntrypoint {
		/**
		 * Runs when an {@link Entity} is caught on fire and the {@link FireType} is set.
		 * @param entity the {@link Entity}
		 * @param fireType the {@link FireType}
		 */
		void onEntityFireTypeSet(Entity entity, Holder<FireType> fireType);
	}

	/**
	 * A functional interface representing an entity burn tick event.
	 */
	@FunctionalInterface
	public interface EntityBurnTick extends CommonEventEntrypoint {
		/**
		 * Runs when an {@link Entity} is burnt from fire lingering on them.
		 * @param entity the burning {@link Entity}
		 * @param fireType the {@link FireType}
		 */
		void onEntityBurnTick(Entity entity, Holder<FireType> fireType);
	}

	/**
	 * A functional interface representing a select fire block state event.
	 */
	@FunctionalInterface
	public interface SelectFireBlockState extends CommonEventEntrypoint {
		/**
		 * Runs when {@link BaseFireBlock#getState(BlockGetter, BlockPos)} is called.
		 * @param level the {@link BlockGetter}
		 * @param belowPos the {@link BlockPos} below the position the fire is being placed
		 * @param belowState the {@link BlockState} below the position the fire is being placed
		 */
		@Nullable
		BlockState selectFireBlockState(BlockGetter level, BlockPos belowPos, BlockState belowState);
	}
}
