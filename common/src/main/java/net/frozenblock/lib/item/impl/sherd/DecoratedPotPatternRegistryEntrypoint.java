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

package net.frozenblock.lib.item.impl.sherd;

import java.util.function.BiConsumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

public interface DecoratedPotPatternRegistryEntrypoint {

	void itemToPatternMappings(BiConsumer<ResourceKey<Item>, ResourceKey<DecoratedPotPattern>> itemToPattern);

	void bootstrap(Registry<DecoratedPotPattern> registry);

	static DecoratedPotPattern register(Registry<DecoratedPotPattern> registry, ResourceKey<DecoratedPotPattern> id, Identifier assetId) {
		return Registry.register(registry, id, new DecoratedPotPattern(assetId));
	}
}
