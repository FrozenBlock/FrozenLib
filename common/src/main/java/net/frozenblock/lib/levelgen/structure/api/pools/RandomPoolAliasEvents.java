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

package net.frozenblock.lib.levelgen.structure.api.pools;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomPoolAlias;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class RandomPoolAliasEvents {
	/**
	 * An event used to modify the contents of a {@link RandomPoolAlias}.
	 */
	public static final Event<Modify> MODIFY = EventRegistry.createEnvironmentEvent(Modify.class,
		callbacks -> (alias, context) -> {
			for (var callback : callbacks) callback.modify(alias, context);
		});


	@FunctionalInterface
	public interface Modify extends CommonEventEntrypoint {
		void modify(ResourceKey<StructureTemplatePool> alias, Context context);
	}

	public static class Context {
		private final List<Weighted<ResourceKey<StructureTemplatePool>>> targets;
		private boolean modified;

		private Context(WeightedList<ResourceKey<StructureTemplatePool>> targets) {
			this.targets = new ArrayList<>(targets.unwrap());
		}

		@ApiStatus.Internal
		public static Context of(WeightedList<ResourceKey<StructureTemplatePool>> targets) {
			return new Context(targets);
		}

		public boolean containsTarget(Identifier target) {
			return this.targets.stream().anyMatch(weighted -> weighted.value().identifier().equals(target));
		}

		public boolean add(Identifier target, int weight) {
			this.targets.add(new Weighted<>(ResourceKey.create(Registries.TEMPLATE_POOL, target), weight));
			this.modified = true;
			return true;
		}

		public boolean setWeightIfPresent(Identifier target, int weight) {
			if (!this.remove(target)) return false;

			this.targets.add(new Weighted<>(ResourceKey.create(Registries.TEMPLATE_POOL, target), weight));
			this.modified = true;
			return true;
		}

		public boolean remove(Identifier target) {
			if (this.targets.removeIf(weighted -> weighted.value().identifier().equals(target))) {
				this.modified = true;
				return true;
			}
			return false;
		}

		public boolean isModified() {
			return this.modified;
		}

		public WeightedList<ResourceKey<StructureTemplatePool>> buildTargets() {
			return WeightedList.of(this.targets);
		}
	}
}
