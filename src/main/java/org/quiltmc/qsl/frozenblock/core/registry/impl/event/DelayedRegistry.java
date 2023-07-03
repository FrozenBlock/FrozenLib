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

package org.quiltmc.qsl.frozenblock.core.registry.impl.event;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class DelayedRegistry<T> implements WritableRegistry<T> {
	private final WritableRegistry<T> wrapped;
	private final Queue<DelayedEntry<T>> delayedEntries = new LinkedList<>();

	DelayedRegistry(WritableRegistry<T> registry) {
		this.wrapped = registry;
	}

	@Override
	public @Nullable ResourceLocation getKey(T entry) {
		return this.wrapped.getKey(entry);
	}

	@Override
	@NotNull
	public Optional<ResourceKey<T>> getResourceKey(T entry) {
		return this.wrapped.getResourceKey(entry);
	}

	@Override
	public int getId(@Nullable T entry) {
		return this.wrapped.getId(entry);
	}

	@Override
	public @Nullable T get(@Nullable ResourceKey<T> entry) {
		return this.wrapped.get(entry);
	}

	@Override
	public @Nullable T get(@Nullable ResourceLocation id) {
		return this.wrapped.get(id);
	}

	@Override
	@NotNull
	public Lifecycle lifecycle(T entry) {
		return this.wrapped.lifecycle(entry);
	}

	@Override
	@NotNull
	public Lifecycle registryLifecycle() {
		return this.wrapped.registryLifecycle();
	}

	@Override
	@NotNull
	public Set<ResourceLocation> keySet() {
		return this.wrapped.keySet();
	}

	@Override
	@NotNull
	public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
		return this.wrapped.entrySet();
	}

	@Override
	@NotNull
	public Set<ResourceKey<T>> registryKeySet() {
		return this.wrapped.registryKeySet();
	}

	@Override
	@NotNull
	public Optional<Reference<T>> getRandom(RandomSource random) {
		return this.wrapped.getRandom(random);
	}

	@Override
	public boolean containsKey(ResourceLocation id) {
		return this.wrapped.containsKey(id);
	}

	@Override
	public boolean containsKey(ResourceKey<T> key) {
		return this.wrapped.containsKey(key);
	}

	@Override
	@NotNull
	public Registry<T> freeze() {
		// Refuse freezing.
		return this;
	}

	@Override
	@NotNull
	public Reference<T> createIntrusiveHolder(T holder) {
		return this.wrapped.createIntrusiveHolder(holder);
	}

	@Override
	@NotNull
	public Optional<Reference<T>> getHolder(int index) {
		return this.wrapped.getHolder(index);
	}

	@Override
	@NotNull
	public Optional<Reference<T>> getHolder(ResourceKey<T> key) {
		return this.wrapped.getHolder(key);
	}

	@Override
	@NotNull
	public Holder<T> wrapAsHolder(T object) {
		return this.wrapped.wrapAsHolder(object);
	}

	@Override
	@NotNull
	public Stream<Reference<T>> holders() {
		return this.wrapped.holders();
	}

	@Override
	@NotNull
	public Optional<Named<T>> getTag(TagKey<T> tag) {
		return this.wrapped.getTag(tag);
	}

	@Override
	@NotNull
	public Named<T> getOrCreateTag(TagKey<T> key) {
		return this.wrapped.getOrCreateTag(key);
	}

	@Override
	@NotNull
	public Stream<Pair<TagKey<T>, Named<T>>> getTags() {
		return this.wrapped.getTags();
	}

	@Override
	@NotNull
	public Stream<TagKey<T>> getTagNames() {
		return this.wrapped.getTagNames();
	}

	@Override
	public void resetTags() {
		throw new UnsupportedOperationException("DelayedRegistry does not support resetTags.");
	}

	@Override
	public void bindTags(Map<TagKey<T>, List<Holder<T>>> tags) {
		throw new UnsupportedOperationException("DelayedRegistry does not support bindTags.");
	}

	@Override
	@NotNull
	public HolderOwner<T> holderOwner() {
		return this.wrapped.holderOwner();
	}

	@Override
	@NotNull
	public RegistryLookup<T> asLookup() {
		return this.wrapped.asLookup();
	}

	@Override
	@NotNull
	public Iterator<T> iterator() {
		return this.wrapped.iterator();
	}

	@Override
	public @Nullable T byId(int index) {
		return this.wrapped.byId(index);
	}

	@Override
	@NotNull
	public ResourceKey<? extends Registry<T>> key() {
		return this.wrapped.key();
	}

	@Override
	public int size() {
		return this.wrapped.size();
	}

	@Override
	@NotNull
	public Holder<T> registerMapping(int rawId, ResourceKey<T> key, T entry, Lifecycle lifecycle) {
		throw new UnsupportedOperationException("DelayedRegistry does not support set.");
	}

	@Override
	@NotNull
	public Reference<T> register(ResourceKey<T> key, T entry, Lifecycle lifecycle) {
		this.delayedEntries.add(new DelayedEntry<>(key, entry, lifecycle));
		return Holder.Reference.createStandAlone(this.wrapped.holderOwner(), key);
	}

	@Override
	public boolean isEmpty() {
		return this.wrapped.isEmpty();
	}

	@Override
	@NotNull
	public HolderGetter<T> createRegistrationLookup() {
		return this.wrapped.createRegistrationLookup();
	}

	void applyDelayed() {
		DelayedEntry<T> entry;

		while ((entry = this.delayedEntries.poll()) != null) {
			this.wrapped.register(entry.key(), entry.entry(), entry.lifecycle());
		}
	}

	record DelayedEntry<T>(ResourceKey<T> key, T entry, Lifecycle lifecycle) {}
}
