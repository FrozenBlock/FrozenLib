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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.world.item.component.BlockTransformerMappings;

/**
 * Allows for the modification of {@link BlockTransformer}s defined in the {@link BlockTransformerMappings} class.
 * <p>
 * These events should be referenced via the {@code frozenlib:events} entrypoint, or they may not work properly.
 */
@UtilityClass
public class BlockTransformerMappingsApi {
	/**
	 * An event used to modify the Shovel's {@link BlockTransformer}s.
	 */
	public static final Event<ModifyShovelBlockTransformer> MODIFY_SHOVEL = FrozenEvents.createEnvironmentEvent(ModifyShovelBlockTransformer.class,
		callbacks -> (context) -> {
			for (var callback : callbacks) callback.modifyShovelBlockTransformer(context);
	});

	/**
	 * An event used to modify the Axe's {@link BlockTransformer}s.
	 */
	public static final Event<ModifyAxeBlockTransformer> MODIFY_AXE = FrozenEvents.createEnvironmentEvent(ModifyAxeBlockTransformer.class,
		callbacks -> (context) -> {
			for (var callback : callbacks) callback.modifyAxeBlockTransformer(context);
	});

	/**
	 * An event used to modify the Hoe's {@link BlockTransformer}s.
	 */
	public static final Event<ModifyHoeBlockTransformer> MODIFY_HOE = FrozenEvents.createEnvironmentEvent(ModifyHoeBlockTransformer.class,
		callbacks -> (context) -> {
			for (var callback : callbacks) callback.modifyHoeBlockTransformer(context);
	});

	@FunctionalInterface
	public interface ModifyShovelBlockTransformer extends CommonEventEntrypoint {
		void modifyShovelBlockTransformer(Context context);
	}

	@FunctionalInterface
	public interface ModifyAxeBlockTransformer extends CommonEventEntrypoint {
		void modifyAxeBlockTransformer(Context context);
	}

	@FunctionalInterface
	public interface ModifyHoeBlockTransformer extends CommonEventEntrypoint {
		void modifyHoeBlockTransformer(Context context);
	}

	public static Context createContext(BlockTransformer blockTransformer) {
		return new Context(blockTransformer);
	}

	public static class Context {
		private final List<BlockTransformer.BlockTransformData> transforms = new ArrayList<>();

		private Context(BlockTransformer blockTransformer) {
			this.transforms.addAll(blockTransformer.transforms());
		}

		public void addFirst(BlockTransformer.BlockTransformData transform) {
			this.transforms.addFirst(transform);
		}

		public void addLast(BlockTransformer.BlockTransformData transform) {
			this.transforms.add(transform);
		}

		public boolean removeIf(Predicate<BlockTransformer.BlockTransformData> predicate) {
			return this.transforms.removeIf(predicate);
		}

		public BlockTransformer toBlockTransformer() {
			return new BlockTransformer(List.copyOf(this.transforms));
		}
	}
}
