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

package org.quiltmc.qsl.frozenblock.core.registry.impl.event;

import java.util.Map;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.neoforged.neoforge.registries.callback.RegistryCallback;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class NeoForgeDelayedRegistry<T> extends DelayedRegistry<T> {

	public NeoForgeDelayedRegistry(WritableRegistry<T> registry) {
		super(registry);
	}

	private IRegistryExtension<T> ext() {
		return this.wrapped;
	}

	@Override
	public boolean doesSync() {
		return ext().doesSync();
	}

	@Override
	public int getMaxId() {
		return ext().getMaxId();
	}

	@Override
	public void addCallback(RegistryCallback<T> callback) {
		ext().addCallback(callback);
	}

	@Override
	public void addAlias(Identifier from, Identifier to) {
		ext().addAlias(from, to);
	}

	@Override
	public Identifier resolve(Identifier name) {
		return ext().resolve(name);
	}

	@Override
	public ResourceKey<T> resolve(ResourceKey<T> key) {
		return ext().resolve(key);
	}

	@Override
	public int getId(ResourceKey<T> key) {
		return ext().getId(key);
	}

	@Override
	public int getId(Identifier name) {
		return ext().getId(name);
	}

	@Override
	public boolean containsValue(T value) {
		return ext().containsValue(value);
	}

	@Override
	public <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
		return ext().getDataMap(type);
	}
}
