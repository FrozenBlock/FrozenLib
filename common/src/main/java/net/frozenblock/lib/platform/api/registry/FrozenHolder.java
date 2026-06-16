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

package net.frozenblock.lib.platform.api.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import java.util.function.Supplier;

/**
 * A cross-platform lazy holder for a registered object.
 * Safe to store as a static field before {@link FrozenDeferredRegister#register()} is called.
 *
 * @param <R> the registry object type
 * @param <T> the concrete subtype, extends R
 */
public interface FrozenHolder<R, T extends R> extends Supplier<T> {

	@Override
	T get();

	ResourceKey<R> getKey();

	Identifier getId();

	boolean isBound();

	Holder<R> asHolder();
}
