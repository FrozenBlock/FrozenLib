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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.registry.DeferredBlock;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredItem;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class FabricDeferredRegister<T> implements DeferredRegister<T> {
	protected final ResourceKey<? extends Registry<T>> registryKey;
	protected final String namespace;

	public FabricDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.registryKey = registryKey;
		this.namespace = namespace;
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
		return this.registerEntry(Identifier.fromNamespaceAndPath(this.namespace, name), supplier, also);
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
		final Identifier id = Identifier.fromNamespaceAndPath(this.namespace, name);
		return this.registerEntry(id, () -> func.apply(id), also);
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also) {
		return this.registerEntry(key.identifier(), supplier, also);
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
		return this.registerEntry(key.identifier(), () -> func.apply(key.identifier()), also);
	}

	@Override
	public void register() {}

	@Override
	public String namespace() {
		return this.namespace;
	}

	@SuppressWarnings("unchecked")
	private <I extends T> DeferredHolder<T, I> registerEntry(Identifier id, Supplier<? extends I> supplier, @Nullable Consumer<I> also) {
		final Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getOptional(this.registryKey.identifier())
			.orElseThrow(() -> new IllegalStateException("No registry found for key: " + this.registryKey.identifier()));

		final ResourceKey<T> key = ResourceKey.create(this.registryKey, id);
		final I value = supplier.get();
		Registry.register(registry, id, value);

		final Holder.Reference<T> holder = registry.getOrThrow(key);
		final FabricDeferredHolder<T, I> frozenHolder = new FabricDeferredHolder<>();
		frozenHolder.bind((Holder.Reference<I>) holder);

		if (also != null) also.accept((I) holder.value());
		return frozenHolder;
	}

	public static class Blocks extends FabricDeferredRegister<Block> implements DeferredRegister.Blocks {
		private FeatureFlag[] requiredFeatures = null;

		public Blocks(String namespace) {
			super(Registries.BLOCK, namespace);
		}

		@Override
		public void setRequiredFeatures(FeatureFlag... flags) {
			this.requiredFeatures = flags;
		}

		@Nullable
		@Override
		public FeatureFlag[] requiredFeatures() {
			return this.requiredFeatures;
		}

		@Override
		public <I extends Block> DeferredBlock<I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
			return new DeferredBlock<>(super.register(name, supplier, also));
		}

		@Override
		public <I extends Block> DeferredBlock<I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new DeferredBlock<>(super.register(name, func, also));
		}

		@Override
		public <I extends Block> DeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier, Consumer<I> also) {
			return new DeferredBlock<>(super.register(key, supplier, also));
		}

		@Override
		public <I extends Block> DeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new DeferredBlock<>(super.register(key, func, also));
		}
	}

	public static class Items extends FabricDeferredRegister<Item> implements DeferredRegister.Items {
		private FeatureFlag[] requiredFeatures = null;

		public Items(String namespace) {
			super(Registries.ITEM, namespace);
		}

		@Override
		public void setRequiredFeatures(FeatureFlag... flags) {
			this.requiredFeatures = flags;
		}

		@Nullable
		@Override
		public FeatureFlag[] requiredFeatures() {
			return this.requiredFeatures;
		}

		@Override
		public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
			return new DeferredItem<>(super.register(name, supplier, also));
		}

		@Override
		public <I extends Item> DeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier, Consumer<I> also) {
			return new DeferredItem<>(super.register(key, supplier, also));
		}

		@Override
		public <I extends Item> DeferredItem<I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new DeferredItem<>(super.register(name, func, also));
		}

		@Override
		public <I extends Item> DeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new DeferredItem<>(super.register(key, func, also));
		}
	}

	public static class DataComponents extends FabricDeferredRegister<DataComponentType<?>> implements DeferredRegister.DataComponents {
		public DataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> registryKey, String namespace) {
			super(registryKey, namespace);
		}
	}

	public static class Entities extends FabricDeferredRegister<EntityType<?>> implements DeferredRegister.Entities {
		private FeatureFlag[] requiredFeatures = null;

		public Entities(String namespace) {
			super(Registries.ENTITY_TYPE, namespace);
		}

		@Override
		public void setRequiredFeatures(FeatureFlag... flags) {
			this.requiredFeatures = flags;
		}

		@Nullable
		@Override
		public FeatureFlag[] requiredFeatures() {
			return this.requiredFeatures;
		}
	}

	public static class SoundEvents extends FabricDeferredRegister<SoundEvent> implements DeferredRegister.SoundEvents {
		public SoundEvents(String namespace) {
			super(Registries.SOUND_EVENT, namespace);
		}
	}

	public static class ParticleTypes extends FabricDeferredRegister<ParticleType<?>> implements DeferredRegister.ParticleTypes {
		public ParticleTypes(String namespace) {
			super(Registries.PARTICLE_TYPE, namespace);
		}
	}

	public static class MemoryModuleTypes extends FabricDeferredRegister<MemoryModuleType<?>> implements DeferredRegister.MemoryModuleTypes {
		public MemoryModuleTypes(String namespace) {
			super(Registries.MEMORY_MODULE_TYPE, namespace);
		}
	}

	public static class Activities extends FabricDeferredRegister<Activity> implements DeferredRegister.Activities {
		public Activities(String namespace) {
			super(Registries.ACTIVITY, namespace);
		}
	}

	public static class SensorTypes extends FabricDeferredRegister<SensorType<?>> implements DeferredRegister.SensorTypes {
		public SensorTypes(String namespace) {
			super(Registries.SENSOR_TYPE, namespace);
		}
	}

	public static class PoiTypes extends FabricDeferredRegister<PoiType> implements DeferredRegister.PoiTypes {
		public PoiTypes(String namespace) {
			super(Registries.POINT_OF_INTEREST_TYPE, namespace);
		}
	}
}
