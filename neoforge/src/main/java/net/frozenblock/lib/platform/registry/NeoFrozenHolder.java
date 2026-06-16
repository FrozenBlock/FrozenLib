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

package net.frozenblock.lib.platform.registry;

import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class NeoFrozenHolder<R, T extends R> implements FrozenHolder<R, T> {

	private final DeferredHolder<R, T> delegate;

	public NeoFrozenHolder(DeferredHolder<R, T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T get() {
		return this.delegate.get();
	}

	@Override
	public ResourceKey<R> getKey() {
		return this.delegate.getKey();
	}

	@Override
	public Identifier getId() {
		return this.delegate.getId();
	}

	@Override
	public boolean isBound() {
		return this.delegate.isBound();
	}

	@Override
	public Holder<R> asHolder() {
		return this.delegate;
	}
}
