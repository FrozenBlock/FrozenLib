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

package net.frozenblock.lib.levelgen.biome.api.modifications;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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
 */

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeModificationImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Provides methods for modifying biomes. To create an instance, call
 * {@link BiomeModifications#create(Identifier)}.
 *
 * @see BiomeModifications
 */
public class BiomeModification {
	private final Identifier id;

	@ApiStatus.Internal
	BiomeModification(Identifier id) {
		this.id = id;
	}

	/**
	 * Adds a modifier that is not sensitive to the current state of the biome when it is applied, examples
	 * for this are modifiers that simply add or remove features unconditionally, or change other values
	 * to constants.
	 */
	public BiomeModification add(
		ModificationPhase phase,
		Predicate<BiomeSelectionContext> selector,
		Consumer<BiomeModificationContext> modifier
	) {
		BiomeModificationImpl.INSTANCE.addModifier(this.id, phase, selector, modifier);
		return this;
	}

	/**
	 * Adds a modifier that is sensitive to the current state of the biome when it is applied.
	 * Examples for this are modifiers that apply scales to existing values (e.g. half the temperature).
	 *
	 * <p>For modifiers that should only be applied if a given condition is met for a Biome, please add these
	 * conditions to the selector, and use a context-free modifier instead, as this will greatly help
	 * with debugging world generation issues.
	 */
	public BiomeModification add(
		ModificationPhase phase,
		Predicate<BiomeSelectionContext> selector,
		BiConsumer<BiomeSelectionContext, BiomeModificationContext> modifier
	) {
		BiomeModificationImpl.INSTANCE.addModifier(this.id, phase, selector, modifier);
		return this;
	}

	/**
	 * Adds a modifier that is sensitive to the current state of the biome when it is applied, and has access to registries.
	 * @see #add(ModificationPhase, Predicate, BiConsumer) this method's docs for further explanation.
	 */
	public BiomeModification add(
		ModificationPhase phase,
		Predicate<BiomeSelectionContext> selector,
		TriConsumer<RegistryAccess, BiomeSelectionContext, BiomeModificationContext> modifier
	) {
		BiomeModificationImpl.INSTANCE.addModifier(this.id, phase, selector, modifier);
		return this;
	}
}
