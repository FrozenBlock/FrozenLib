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

package net.frozenblock.lib.levelgen.material.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

@UtilityClass
public final class FrozenLibMaterialConditions {
	public static final ResourceKey<MaterialCondition> AVOID_BEDROCK_FLOOR = createKey("avoid_bedrock_floor");
	public static final ResourceKey<MaterialCondition> AVOID_BEDROCK_ROOF = createKey("avoid_bedrock_roof");

	public static void bootstrap(BootstrapContext<MaterialCondition> context) {
		context.register(
			AVOID_BEDROCK_FLOOR,
			MaterialRules.not(
				MaterialRules.verticalGradient(
					"bedrock_floor",
					VerticalAnchor.bottom(),
					VerticalAnchor.aboveBottom(5)
				)
			)
		);
		context.register(
			AVOID_BEDROCK_ROOF,
			MaterialRules.verticalGradient(
				"bedrock_roof",
				VerticalAnchor.belowTop(5),
				VerticalAnchor.top()
			)
		);
	}

	private static ResourceKey<MaterialCondition> createKey(String name) {
		return ResourceKey.create(Registries.MATERIAL_CONDITION, FrozenLibConstants.id(name));
	}
}
