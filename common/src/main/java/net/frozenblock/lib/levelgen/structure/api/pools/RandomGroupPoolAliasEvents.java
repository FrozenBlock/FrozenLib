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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.DirectPoolAlias;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomGroupPoolAlias;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomPoolAlias;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class RandomGroupPoolAliasEvents {
	/**
	 * An event used to modify the contents of a {@link RandomGroupPoolAlias}.
	 */
	public static final Event<Modify> MODIFY = EventRegistry.createEnvironmentEvent(Modify.class,
		callbacks -> context -> {
			for (var callback : callbacks) callback.modify(context);
		});


	@FunctionalInterface
	public interface Modify extends CommonEventEntrypoint {
		void modify(Context context);
	}

	public static class Context {
		private final List<Weighted<List<PoolAliasBinding>>> groups;
		private boolean modified;

		private Context(WeightedList<List<PoolAliasBinding>> groups) {
			final List<Weighted<List<PoolAliasBinding>>> newGroups = new ArrayList<>();
			groups.unwrap().forEach(weighted -> newGroups.add(new Weighted<>(new ArrayList<>(weighted.value()), weighted.weight())));

			this.groups = newGroups;
		}

		@ApiStatus.Internal
		public static Context of(WeightedList<List<PoolAliasBinding>> groups) {
			return new Context(groups);
		}

		public boolean containsSubAlias(Identifier alias) {
			for (Weighted<List<PoolAliasBinding>> group : this.groups) {
				for (PoolAliasBinding aliasBinding : group.value()) {
					if (aliasBinding instanceof DirectPoolAlias direct && direct.alias().identifier().equals(alias)) return true;
					if (aliasBinding instanceof RandomPoolAlias random && random.alias().identifier().equals(alias)) return true;
				}
			}

			return false;
		}

		public boolean containsSubTarget(Identifier target) {
			for (Weighted<List<PoolAliasBinding>> group : this.groups) {
				for (PoolAliasBinding aliasBinding : group.value()) {
					if (aliasBindingContainsTarget(aliasBinding, target)) return true;
				}
			}

			return false;
		}

		public boolean add(List<PoolAliasBinding> group, int weight) {
			this.groups.add(new Weighted<>(group, weight));
			this.modified = true;
			return true;
		}

		public boolean setWeightOfAllWithSubTargetIfPresent(Identifier target, int weight) {
			final List<Weighted<List<PoolAliasBinding>>> newGroups = new ArrayList<>();

			boolean modifiedAny = false;
			for (Weighted<List<PoolAliasBinding>> group : this.groups) {
				if (group.value().stream().anyMatch(aliasBinding -> aliasBindingContainsTarget(aliasBinding, target))) {
					newGroups.add(new Weighted<>(group.value(), weight));
					this.modified = true;
					modifiedAny = true;
				} else {
					newGroups.add(group);
				}
			}

			this.groups.clear();
			this.groups.addAll(newGroups);

			return modifiedAny;
		}

		public boolean removeAllWithSubTarget(Identifier target) {
			boolean removedAny = false;
			for (Weighted<List<PoolAliasBinding>> group : this.groups) {
				if (group.value().removeIf(aliasBinding -> aliasBindingContainsTarget(aliasBinding, target))) {
					removedAny = true;
					this.modified = true;
				}
			}

			return removedAny;
		}

		private static boolean aliasBindingContainsTarget(PoolAliasBinding aliasBinding, Identifier target) {
			if (aliasBinding instanceof DirectPoolAlias direct && direct.target().identifier().equals(target)) return true;
			if (aliasBinding instanceof RandomPoolAlias random) {
				for (Weighted<ResourceKey<StructureTemplatePool>> targetKey : random.targets().unwrap()) {
					if (targetKey.value().identifier().equals(target)) return true;
				}
			}

			return false;
		}

		public boolean isModified() {
			return this.modified;
		}

		public WeightedList<List<PoolAliasBinding>> buildGroups() {
			return WeightedList.of(this.groups);
		}
	}
}
