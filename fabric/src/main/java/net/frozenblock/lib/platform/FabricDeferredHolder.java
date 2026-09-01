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

package net.frozenblock.lib.platform;

import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class FabricDeferredHolder<R, T extends R> implements DeferredHolder<R, T> {

	private Holder.Reference<T> reference;

	void bind(Holder.Reference<T> reference) {
		this.reference = reference;
	}

	private Holder.Reference<T> requireBound() {
		if (this.reference == null) throw new IllegalStateException("FrozenHolder not yet bound; call FrozenDeferredRegister.register() first");
		return this.reference;
	}

	@Override
	public T get() {
		return requireBound().value();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResourceKey<R> getKey() {
		return (ResourceKey<R>) requireBound().key();
	}

	@Override
	public Identifier getId() {
		return requireBound().key().identifier();
	}

	@Override
	public boolean isBound() {
		return this.reference != null && this.reference.isBound();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Holder<R> asHolder() {
		return (Holder<R>) requireBound();
	}
}
