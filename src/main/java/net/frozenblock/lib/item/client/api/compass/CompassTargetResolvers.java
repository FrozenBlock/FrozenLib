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

package net.frozenblock.lib.item.client.api.compass;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class CompassTargetResolvers {
	private static final List<CompassTargetResolver> RESOLVERS = new CopyOnWriteArrayList<>();

	private CompassTargetResolvers() {}

	public static void register(CompassTargetResolver resolver) {
		RESOLVERS.add(resolver);
	}

	public static Optional<CompassTarget> resolve(ClientLevel level, ItemStack stack) {
		for (CompassTargetResolver resolver : RESOLVERS) {
			final Optional<CompassTarget> target = resolver.resolve(level, stack);
			if (target != null && target.isPresent()) return target;
		}
		return Optional.empty();
	}

	public static boolean isEmpty() {
		return RESOLVERS.isEmpty();
	}
}
