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

package net.frozenblock.lib.levelgen.structure.api.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

@UtilityClass
public class StructureGenerationConditionApi {
	/**
	 * An event used to add new conditions for a {@link StructureSet} to generate.
	 */
	public static final Event<AddGenerationCondition> ADD_GENERATION_CONDITIONS = EventRegistry.createEnvironmentEvent(AddGenerationCondition.class,
		callbacks -> (structureSet, context) -> {
			for (var callback : callbacks) callback.addGenerationConditions(structureSet, context);
		});

	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register(registryAccess -> {
			registryAccess.lookup(Registries.STRUCTURE_SET).ifPresent(structureSetRegistry -> {
				structureSetRegistry.listElements().forEach(structureSet -> {
					final Context context = new Context();
					ADD_GENERATION_CONDITIONS.invoker().addGenerationConditions(structureSet, context);
					structureSet.value().frozenLib$addGenerationConditions(context.conditions);
				});
			});
		});
	}

	@FunctionalInterface
	public interface AddGenerationCondition extends CommonEventEntrypoint {
		void addGenerationConditions(Holder<StructureSet> structureSet, Context context);
	}

	public final class Context {
		private final List<Supplier<Boolean>> conditions;

		private Context() {
			this.conditions = new ArrayList<>();
		}

		public void add(Supplier<Boolean> condition) {
			this.conditions.add(condition);
		}
	}
}
