/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
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

	@NotNull
	@Override
	public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> key) {
		return this.wrapped.registrationInfo(key);
	}

	@Override
	@NotNull
	public Lifecycle registryLifecycle() {
		return this.wrapped.registryLifecycle();
	}

	@Override
	@NotNull
	public Optional<Reference<T>> getAny() {
		return this.wrapped.getAny();
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
	public Optional<Reference<T>> getHolder(ResourceLocation key) {
		return this.wrapped.getHolder(key);
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
	public Stream<Named<T>> getTags() {
		return this.wrapped.getTags();
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
	public PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> loadResult) {
		return this.wrapped.prepareTagReload(loadResult);
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

	@NotNull
	@Override
	public Reference<T> register(ResourceKey<T> key, T entry, RegistrationInfo registrationInfo) {
		this.delayedEntries.add(new DelayedEntry<>(key, entry, registrationInfo));
		return Holder.Reference.createStandAlone(this.wrapped.holderOwner(), key);
	}

	@Override
	public void bindTag(TagKey<T> tagKey, List<Holder<T>> list) {
		this.wrapped.bindTag(tagKey, list);
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
			this.wrapped.register(entry.key(), entry.entry(), entry.registrationInfo);
		}
	}

	record DelayedEntry<T>(ResourceKey<T> key, T entry, RegistrationInfo registrationInfo) {}
}
