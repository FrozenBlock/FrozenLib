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

package net.frozenblock.lib.levelgen.biome.mixin;

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

import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeModificationImpl;
import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeModificationMarker;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * This Mixin allows us to keep backup copies of biomes for
 * {@link BiomeModificationImpl} on a per-RegistryAccess basis.
 */
@Mixin(RegistryAccess.ImmutableRegistryAccess.class)
public class RegistryAccessImmutableRegistryAccessMixin implements BiomeModificationMarker {
	@Unique
	private boolean frozenLib$modified;

	@Override
	public void frozenLib$markModified() {
		if (frozenLib$modified) {
			throw new IllegalStateException("This dynamic registries instance has already been modified");
		}

		frozenLib$modified = true;
	}
}
