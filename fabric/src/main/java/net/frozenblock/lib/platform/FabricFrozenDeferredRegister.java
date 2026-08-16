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
import java.util.function.UnaryOperator;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredBlock;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredItem;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

public class FabricFrozenDeferredRegister<T> implements FrozenDeferredRegister<T> {
	protected final ResourceKey<? extends Registry<T>> registryKey;
	protected final String namespace;

	public FabricFrozenDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.registryKey = registryKey;
		this.namespace = namespace;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier) {
		return register(name, supplier, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
		return registerEntry(Identifier.fromNamespaceAndPath(this.namespace, name), supplier, also);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
		return register(name, func, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
		final Identifier id = Identifier.fromNamespaceAndPath(this.namespace, name);
		return registerEntry(id, () -> func.apply(id), also);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier) {
		return register(key, supplier, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also) {
		return registerEntry(key.identifier(), supplier, also);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func) {
		return register(key, func, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
		return registerEntry(key.identifier(), () -> func.apply(key.identifier()), also);
	}

	@Override
	public void register() {}

	@Override
	public String namespace() {
		return this.namespace;
	}

	@SuppressWarnings("unchecked")
	private <I extends T> FrozenHolder<T, I> registerEntry(Identifier id, Supplier<? extends I> supplier, @Nullable Consumer<I> also) {
		final Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getOptional(this.registryKey.identifier())
			.orElseThrow(() -> new IllegalStateException("No registry found for key: " + this.registryKey.identifier()));

		final ResourceKey<T> key = ResourceKey.create(this.registryKey, id);
		final I value = supplier.get();
		Registry.register(registry, id, value);

		final Holder.Reference<T> holder = registry.getOrThrow(key);
		final FabricFrozenHolder<T, I> frozenHolder = new FabricFrozenHolder<>();
		frozenHolder.bind((Holder.Reference<I>) holder);

		if (also != null) also.accept((I) holder.value());
		return frozenHolder;
	}

	public static class Blocks extends FabricFrozenDeferredRegister<Block> implements FrozenDeferredRegister.Blocks {

		public Blocks(String namespace) {
			super(Registries.BLOCK, namespace);
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(String name, Supplier<? extends I> supplier) {
			return new FrozenDeferredBlock<>(super.register(name, supplier));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
			return new FrozenDeferredBlock<>(super.register(name, supplier, also));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(String name, Function<Identifier, ? extends I> func) {
			return new FrozenDeferredBlock<>(super.register(name, func));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new FrozenDeferredBlock<>(super.register(name, func, also));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier) {
			return new FrozenDeferredBlock<>(super.register(key, supplier));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier, Consumer<I> also) {
			return new FrozenDeferredBlock<>(super.register(key, supplier, also));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func) {
			return new FrozenDeferredBlock<>(super.register(key, func));
		}

		@Override
		public <I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new FrozenDeferredBlock<>(super.register(key, func, also));
		}
	}

	public static class Items extends FabricFrozenDeferredRegister<Item> implements FrozenDeferredRegister.Items {

		public Items(String namespace) {
			super(Registries.ITEM, namespace);
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(String name, Supplier<? extends I> supplier) {
			return new FrozenDeferredItem<>(super.register(name, supplier));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
			return new FrozenDeferredItem<>(super.register(name, supplier, also));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier) {
			return new FrozenDeferredItem<>(super.register(key, supplier));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier, Consumer<I> also) {
			return new FrozenDeferredItem<>(super.register(key, supplier, also));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(String name, Function<Identifier, ? extends I> func) {
			return new FrozenDeferredItem<>(super.register(name, func));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new FrozenDeferredItem<>(super.register(name, func, also));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func) {
			return new FrozenDeferredItem<>(super.register(key, func));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
			return new FrozenDeferredItem<>(super.register(key, func, also));
		}
	}

	public static class DataComponents extends FabricFrozenDeferredRegister<DataComponentType<?>> implements FrozenDeferredRegister.DataComponents {

		public DataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> registryKey, String namespace) {
			super(registryKey, namespace);
		}

		@Override
		public <D> FrozenHolder<DataComponentType<?>, DataComponentType<D>> registerComponent(String name, UnaryOperator<DataComponentType.Builder<D>> builder) {
			return register(name, () -> builder.apply(DataComponentType.builder()).build());
		}
	}

	public static class Entities extends FabricFrozenDeferredRegister<EntityType<?>> implements FrozenDeferredRegister.Entities {

		public Entities(String namespace) {
			super(Registries.ENTITY_TYPE, namespace);
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category) {
			var id = Identifier.fromNamespaceAndPath(this.namespace, name);
			var key = ResourceKey.create(Registries.ENTITY_TYPE, id);
			return register(name, () -> EntityType.Builder.of(factory, category).build(key));
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, Consumer<EntityType<E>> also) {
			var id = Identifier.fromNamespaceAndPath(this.namespace, name);
			var key = ResourceKey.create(Registries.ENTITY_TYPE, id);
			return register(name, () -> EntityType.Builder.of(factory, category).build(key), also);
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder) {
			return register(name, () -> {
				var id = Identifier.fromNamespaceAndPath(this.namespace, name);
				var key = ResourceKey.create(Registries.ENTITY_TYPE, id);
				var b = EntityType.Builder.of(factory, category);
				return builder.apply(b).build(key);
			});
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder, Consumer<EntityType<E>> also) {
			return register(name, () -> {
				var id = Identifier.fromNamespaceAndPath(this.namespace, name);
				var key = ResourceKey.create(Registries.ENTITY_TYPE, id);
				var b = EntityType.Builder.of(factory, category);
				return builder.apply(b).build(key);
			}, also);
		}
	}
}
