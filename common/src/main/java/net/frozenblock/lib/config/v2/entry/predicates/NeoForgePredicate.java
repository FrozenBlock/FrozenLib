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

package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.platform.ModLoader;

public class NeoForgePredicate implements ConfigPredicate {
	public static NeoForgePredicate INSTANCE = new NeoForgePredicate();
	public static final MapCodec<NeoForgePredicate> CODEC = MapCodec.unit(() -> INSTANCE);

	private NeoForgePredicate() {}

	@Override
	public Boolean get() {
		return ModLoader.isNeoForge();
	}

	@Override
	public MapCodec<NeoForgePredicate> codec() {
		return CODEC;
	}
}
