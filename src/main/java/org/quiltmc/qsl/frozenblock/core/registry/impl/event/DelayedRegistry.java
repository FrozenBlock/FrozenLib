/*
 * Copyright 2022 QuiltMC
 * Copyright 2022 FrozenBlock
 * Modified to work on Fabric
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

package org.quiltmc.qsl.frozenblock.core.registry.impl.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
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
public final class DelayedRegistry<T> extends WritableRegistry<T> {
	private final WritableRegistry<T> wrapped;
	private final Queue<DelayedEntry<T>> delayedEntries = new LinkedList<>();

	DelayedRegistry(WritableRegistry<T> registry) {
		super(registry.key(), registry.elementsLifecycle());

		this.wrapped = registry;
	}

	@Override
	public @Nullable ResourceLocation getKey(T entry) {
		return this.wrapped.getKey(entry);
	}

	@Override
	public Optional<ResourceKey<T>> getResourceKey(T entry) {
		return this.wrapped.getResourceKey(entry);
	}

	@Override
	public int getId(@Nullable T entry) {
		return this.wrapped.getId(entry);
	}

	@Override
	public @Nullable T byId(int index) {
		return this.wrapped.byId(index);
	}

	@Override
	public int size() {
		return this.wrapped.size();
	}

	@Override
	public @Nullable T get(@Nullable ResourceKey<T> key) {
		return this.wrapped.get(key);
	}

	@Override
	public @Nullable T get(@Nullable ResourceLocation id) {
		return this.wrapped.get(id);
	}

	@Override
	public Lifecycle lifecycle(T object) {
		return this.wrapped.lifecycle(object);
	}

	@Override
	public Lifecycle elementsLifecycle() {
		return this.wrapped.elementsLifecycle();
	}

	@Override
	public Set<ResourceLocation> keySet() {
		return this.wrapped.keySet();
	}

	@Override
	public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
		return this.wrapped.entrySet();
	}

	@Override
	public Set<ResourceKey<T>> registryKeySet() {
		return this.wrapped.registryKeySet();
	}

	@Override
	public Optional<Holder<T>> getRandom(RandomSource random) {
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
	public Registry<T> freeze() {
		// Refuse freezing.
		return this;
	}

	@Override
	public Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> registryKey) {
		return this.wrapped.getOrCreateHolderOrThrow(registryKey);
	}

	@Override
	public DataResult<Holder.Reference<T>> getOrCreateHolder(ResourceKey<T> key) {
		return this.wrapped.getOrCreateHolder(key);
	}

	@Override
	public Holder.Reference<T> createIntrusiveHolder(T holder) {
		return this.wrapped.createIntrusiveHolder(holder);
	}

	@Override
	public Optional<Holder.Reference<T>> getHolder(int index) {
		return this.wrapped.getHolder(index);
	}

	@Override
	public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key) {
		return this.wrapped.getHolder(key);
	}

	@Override
	public Stream<Holder.Reference<T>> holders() {
		return this.wrapped.holders();
	}

	@Override
	public Optional<HolderSet.Named<T>> getTag(TagKey<T> tag) {
		return this.wrapped.getTag(tag);
	}

	@Override
	public HolderSet.Named<T> getOrCreateTag(TagKey<T> key) {
		return this.wrapped.getOrCreateTag(key);
	}

	@Override
	public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
		return this.wrapped.getTags();
	}

	@Override
	public Stream<TagKey<T>> getTagNames() {
		return this.wrapped.getTagNames();
	}

	@Override
	public boolean isKnownTagName(TagKey<T> tag) {
		return this.wrapped.isKnownTagName(tag);
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
	public @NotNull Iterator<T> iterator() {
		return this.wrapped.iterator();
	}

	@Override
	public Holder<T> registerMapping(int rawId, ResourceKey<T> key, T entry, Lifecycle lifecycle) {
		throw new UnsupportedOperationException("DelayedRegistry does not support set.");
	}

	@Override
	public Holder<T> register(ResourceKey<T> key, T entry, Lifecycle lifecycle) {
		this.delayedEntries.add(new DelayedEntry<>(key, entry, lifecycle));
		return new Holder.Direct<>(entry);
	}

	@Override
	public boolean isEmpty() {
		return this.wrapped.isEmpty();
	}

	void applyDelayed() {
		DelayedEntry<T> entry;

		while ((entry = this.delayedEntries.poll()) != null) {
			this.wrapped.register(entry.key(), entry.entry(), entry.lifecycle());
		}
	}

	record DelayedEntry<T>(ResourceKey<T> key, T entry, Lifecycle lifecycle) {
	}
}
