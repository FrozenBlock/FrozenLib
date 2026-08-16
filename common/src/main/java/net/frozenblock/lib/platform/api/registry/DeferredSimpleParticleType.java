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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class DeferredSimpleParticleType implements DeferredHolder<ParticleType<?>, SimpleParticleType> {
	private final DeferredHolder<ParticleType<?>, SimpleParticleType> holder;

	public DeferredSimpleParticleType(DeferredHolder<ParticleType<?>, SimpleParticleType> holder) {
		this.holder = holder;
	}

	@Override
	public SimpleParticleType get() {
		return this.holder.get();
	}

	@Override
	public ResourceKey<ParticleType<?>> getKey() {
		return this.holder.getKey();
	}

	@Override
	public Identifier getId() {
		return this.holder.getId();
	}

	@Override
	public boolean isBound() {
		return this.holder.isBound();
	}

	@Override
	public Holder<ParticleType<?>> asHolder() {
		return this.holder.asHolder();
	}
}
