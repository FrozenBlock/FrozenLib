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

import net.frozenblock.lib.platform.api.registry.FrozenDeferredBlock;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredItem;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FabricFrozenDeferredRegister<T> implements FrozenDeferredRegister<T> {

	protected final ResourceKey<? extends Registry<T>> registryKey;
	protected final String namespace;
	protected final List<PendingEntry<T, ?>> pending = new ArrayList<>();

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
		FabricFrozenHolder<T, I> holder = new FabricFrozenHolder<>();
		this.pending.add(new PendingEntry<>(Identifier.fromNamespaceAndPath(this.namespace, name), supplier, also, holder));
		return holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
		return register(name, func, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
		FabricFrozenHolder<T, I> holder = new FabricFrozenHolder<>();
		var id = Identifier.fromNamespaceAndPath(this.namespace, name);
		this.pending.add(new PendingEntry<>(id, () -> func.apply(id), (Consumer) also, holder));
		return holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier) {
		return register(key, supplier, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also) {
		FabricFrozenHolder<T, I> holder = new FabricFrozenHolder<>();
		this.pending.add(new PendingEntry<>(key.identifier(), supplier, also, holder));
		return holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func) {
		return register(key, func, null);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
		FabricFrozenHolder<T, I> holder = new FabricFrozenHolder<>();
		this.pending.add(new PendingEntry<>(key.identifier(), () -> func.apply(key.identifier()), (Consumer) also, holder));
		return holder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void register() {
		Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getOptional(this.registryKey.identifier())
			.orElseThrow(() -> new IllegalStateException("No registry found for key: " + this.registryKey.identifier()));
		for (PendingEntry<T, ?> entry : this.pending) {
			registerEntry(registry, entry);
		}
		this.pending.clear();
	}

	@SuppressWarnings("unchecked")
	private <I extends T> void registerEntry(Registry<T> registry, PendingEntry<T, I> entry) {
		ResourceKey<T> key = ResourceKey.create(this.registryKey, entry.id());
		I value = entry.supplier().get();
		Registry.register(registry, entry.id(), value);
		Holder.Reference<T> ref = registry.getOrThrow(key);
		entry.holder().bind((Holder.Reference<I>) ref);
		var also = entry.also();
		if (also != null)
			also.accept((I) ref.value());
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

		@Override
		public <B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
			return new FrozenDeferredBlock<>(register(key, () -> func.apply(properties.get().setId(key))));
		}

		@Override
		public <B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, Consumer<B> also) {
			return new FrozenDeferredBlock<>(register(key, () -> func.apply(properties.get().setId(key)), also));
		}

		@Override
		public <B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> propertiesOp) {
			return registerBlock(key, func, () -> propertiesOp.apply(BlockBehaviour.Properties.of()));
		}

		@Override
		public <B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties, Consumer<B> also) {
			return registerBlock(key, func, () -> properties.apply(BlockBehaviour.Properties.of()), also);
		}

		@Override
		public <B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func) {
			return registerBlock(key, func, BlockBehaviour.Properties::of);
		}

		@Override
		public <B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, Consumer<B> also) {
			return registerBlock(key, func, BlockBehaviour.Properties::of, also);
		}

		@Override
		public FrozenDeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key, Supplier<BlockBehaviour.Properties> properties) {
			return registerBlock(key, Block::new, properties);
		}

		@Override
		public FrozenDeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key, UnaryOperator<BlockBehaviour.Properties> propertiesOp) {
			return registerBlock(key, Block::new, propertiesOp);
		}

		@Override
		public FrozenDeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key) {
			return registerBlock(key, Block::new);
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

		@Override
		public <I extends Item> FrozenDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
			return new FrozenDeferredItem<>(register(name, () -> func.apply(properties.get())));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, UnaryOperator<Item.Properties> propertiesOp) {
			return registerItem(name, func, () -> propertiesOp.apply(new Item.Properties()));
		}

		@Override
		public <I extends Item> FrozenDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
			return registerItem(name, func, Item.Properties::new);
		}

		@Override
		public FrozenDeferredItem<Item> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
			return registerItem(name, Item::new, properties);
		}

		@Override
		public FrozenDeferredItem<Item> registerSimpleItem(String name, UnaryOperator<Item.Properties> propertiesOp) {
			return registerItem(name, Item::new, propertiesOp);
		}

		@Override
		public FrozenDeferredItem<Item> registerSimpleItem(String name) {
			return registerItem(name, Item::new);
		}

		@Override
		public FrozenDeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, Supplier<Item.Properties> properties) {
			return registerItem(name, props -> new BlockItem(block.get(), props), () -> properties.get().useBlockDescriptionPrefix());
		}

		@Override
		public FrozenDeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, UnaryOperator<Item.Properties> propertiesOp) {
			return registerSimpleBlockItem(name, block, () -> propertiesOp.apply(new Item.Properties()));
		}

		@Override
		public FrozenDeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block) {
			return registerSimpleBlockItem(name, block, Item.Properties::new);
		}

		@Override
		public FrozenDeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, Supplier<Item.Properties> properties) {
			return registerSimpleBlockItem(block.unwrapKey().orElseThrow().identifier().getPath(), block::value, properties);
		}

		@Override
		public FrozenDeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, UnaryOperator<Item.Properties> propertiesOp) {
			return registerSimpleBlockItem(block, () -> propertiesOp.apply(new Item.Properties()));
		}

		@Override
		public FrozenDeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block) {
			return registerSimpleBlockItem(block, Item.Properties::new);
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
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder) {
			return register(name, () -> {
				var id = Identifier.fromNamespaceAndPath(this.namespace, name);
				var key = ResourceKey.create(Registries.ENTITY_TYPE, id);
				var b = EntityType.Builder.of(factory, category);
				return builder.apply(b).build(key);
			});
		}
	}

	private record PendingEntry<T, I extends T>(
		Identifier id,
		Supplier<? extends I> supplier,
		@Nullable Consumer<I> also,
		FabricFrozenHolder<T, I> holder
	) {}
}
