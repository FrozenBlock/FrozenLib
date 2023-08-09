/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.tag.api;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class FrozenBiomeTags {

	private FrozenBiomeTags() {
		throw new UnsupportedOperationException("FrozenBiomeTags contains only static declarations.");
	}

	public static final TagKey<Biome> CAN_LIGHTNING_OVERRIDE = of("can_lightning_override");
	public static final TagKey<Biome> CANNOT_LIGHTNING_OVERRIDE = of("cannot_lightning_override");

	private static TagKey<Biome> of(String path) {
		return TagKey.create(Registries.BIOME, FrozenMain.id(path));
	}
}
