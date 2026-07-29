/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.advancement.impl;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;

public interface AdvancementInterface {

	default AdvancementRewards frozenLib$getRewards() {
		throw new AssertionError();
	}

	default void frozenLib$setRewards(AdvancementRewards rewards) {
		throw new AssertionError();
	}

	default AdvancementRequirements frozenLib$getRequirements() {
		throw new AssertionError();
	}

	default AdvancementRequirementsInterface frozenLib$getRequirementsInterface() {
		throw new AssertionError();
	}

	default void frozenLib$setRequirements(AdvancementRequirements requirements) {
		throw new AssertionError();
	}

	default Map<String, Criterion<?>> frozenLib$getCriteria() {
		throw new AssertionError();
	}

	default void frozenLib$setCriteria(HashMap<String, Criterion<?>> criteria) {
		throw new AssertionError();
	}
}
