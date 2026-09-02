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

package net.frozenblock.lib.item.api.component;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;

/**
 * Allows for the modification of {@link BlockTransformer}s as they're loaded from resources.
 */
@UtilityClass
public class BlockTransformerEvents {
	/**
	 * An event used to modify the Axe's {@link BlockTransformer}s.
	 */
	public static final Event<ModifyAxeBlockTransformer> MODIFY_AXE = EventRegistry.createEnvironmentEvent(ModifyAxeBlockTransformer.class,
		callbacks -> (context, registries) -> {
			for (var callback : callbacks) callback.modifyAxeBlockTransformer(context, registries);
	});

	/**
	 * An event used to quickly add new strippable {@link BlockTransformer}s for the Axe.
	 * <p>
	 * While you can still use {@link #MODIFY_AXE} and add strippable transformations that way,
	 * this event cuts down the amount of duplicate implementation required.
	 */
	public static final Event<AddAxeStrippable> ADD_AXE_STRIPPABLE = EventRegistry.createEnvironmentEvent(AddAxeStrippable.class,
		callbacks -> (context, addStrippable, registries) -> {
			for (var callback : callbacks) callback.addAxeStrippables(context, addStrippable, registries);
		});

	/**
	 * An event used to modify the Hoe's {@link BlockTransformer}s.
	 */
	public static final Event<ModifyHoeBlockTransformer> MODIFY_HOE = EventRegistry.createEnvironmentEvent(ModifyHoeBlockTransformer.class,
		callbacks -> (context, registries) -> {
			for (var callback : callbacks) callback.modifyHoeBlockTransformer(context, registries);
	});

	/**
	 * An event used to modify the Shovel's {@link BlockTransformer}s.
	 */
	public static final Event<ModifyShovelBlockTransformer> MODIFY_SHOVEL = EventRegistry.createEnvironmentEvent(ModifyShovelBlockTransformer.class,
		callbacks -> (context, registries) -> {
			for (var callback : callbacks) callback.modifyShovelBlockTransformer(context, registries);
		});

	/**
	 * An event used to modify {@link BlockTransformer}s, when none of the other events apply.
	 */
	public static final Event<ModifyBlockTransformer> MODIFY = EventRegistry.createEnvironmentEvent(ModifyBlockTransformer.class,
		callbacks -> (key, context, registries) -> {
			for (var callback : callbacks) callback.modifyBlockTransformer(key, context, registries);
		});

	/**
	 * An event used to run custom logic upon a {@link BlockTransformer} transforming a {@link Block}.
	 */
	public static final Event<OnTransform> ON_TRANSFORM = EventRegistry.createEnvironmentEvent(OnTransform.class,
		callbacks -> (context, player, level, pos, itemInHand, oldState, newState) -> {
			for (var callback : callbacks) callback.onTransform(context, player, level, pos, itemInHand, oldState, newState);
		});

	@FunctionalInterface
	public interface ModifyAxeBlockTransformer extends CommonEventEntrypoint {
		void modifyAxeBlockTransformer(Context context, RegistryOps.RegistryInfoLookup registries);
	}

	@FunctionalInterface
	public interface AddAxeStrippable extends CommonEventEntrypoint {
		void addAxeStrippables(
			AxeStrippablesContext context,
			BiConsumer<Block, Block> addStrippable,
			RegistryOps.RegistryInfoLookup registries
		);
	}

	@FunctionalInterface
	public interface ModifyHoeBlockTransformer extends CommonEventEntrypoint {
		void modifyHoeBlockTransformer(Context context, RegistryOps.RegistryInfoLookup registries);
	}

	@FunctionalInterface
	public interface ModifyShovelBlockTransformer extends CommonEventEntrypoint {
		void modifyShovelBlockTransformer(Context context, RegistryOps.RegistryInfoLookup registries);
	}

	@FunctionalInterface
	public interface ModifyBlockTransformer extends CommonEventEntrypoint {
		void modifyBlockTransformer(ResourceKey<BlockTransformer> key, Context context, RegistryOps.RegistryInfoLookup registries);
	}

	@FunctionalInterface
	public interface OnTransform extends CommonEventEntrypoint {
		void onTransform(UseOnContext context, Player player, Level level, BlockPos pos, ItemStack itemInHand, BlockState oldState, BlockState newState);
	}

	public static Context createContext(BlockTransformer blockTransformer) {
		return new Context(blockTransformer);
	}

	public static AxeStrippablesContext createAxeStrippablesContext() {
		return new AxeStrippablesContext();
	}

	public static class Context {
		private final List<BlockTransformer.BlockTransformData> transforms = new ArrayList<>();
		private boolean modified = false;

		private Context(BlockTransformer blockTransformer) {
			this.transforms.addAll(blockTransformer.transforms());
		}

		public void addFirst(BlockTransformer.BlockTransformData transform) {
			this.transforms.addFirst(transform);
			this.modified = true;
		}

		public void addLast(BlockTransformer.BlockTransformData transform) {
			this.transforms.add(transform);
			this.modified = true;
		}

		public boolean removeIf(Predicate<BlockTransformer.BlockTransformData> predicate) {
			final boolean removedAny = this.transforms.removeIf(predicate);
			if (removedAny) this.modified = true;
			return removedAny;
		}

		public boolean modified() {
			return this.modified;
		}

		public BlockTransformer toBlockTransformer() {
			return new BlockTransformer(List.copyOf(this.transforms));
		}
	}

	public static class AxeStrippablesContext {
		private final List<Pair<BlockPredicate, BlockStateProvider>> strippables = new ArrayList<>();
		private boolean modified = false;

		private AxeStrippablesContext() {}

		public void add(BlockPredicate ifTrue, BlockStateProvider then) {
			this.strippables.addFirst(Pair.of(ifTrue, then));
			this.modified = true;
		}

		public void addLast(BlockPredicate ifTrue, BlockStateProvider then) {
			this.strippables.add(Pair.of(ifTrue, then));
			this.modified = true;
		}

		public boolean modified() {
			return this.modified;
		}

		public BlockTransformer.BlockTransformData toBlockTransformData() {
			final RuleBasedStateProvider.Builder builder = RuleBasedStateProvider.builder();
			this.strippables.forEach(pair -> builder.ifTrueThenProvide(pair.getFirst(), pair.getSecond()));
			return BlockTransformer.BlockTransformData.builder(builder.build()).sound(SoundEvents.AXE_STRIP).build();
		}
	}
}
