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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A cross-platform deferred registry abstraction, analogous to NeoForge's {@code DeferredRegister}.
 *
 * <p>Create with {@link #create(ResourceKey, String)}, queue entries with
 * {@link #register(String, Supplier)}, then call {@link #register()} during mod initialization.
 *
 * <p>On Fabric: {@link #register()} immediately registers all queued entries.
 * On NeoForge: {@link #register()} delegates to NeoForge's DeferredRegister and hooks
 * the active mod's event bus via {@code ModLoadingContext}. Must be called during mod init.
 *
 * @param <T> the registry value type
 */
public interface FrozenDeferredRegister<T> {

	static <T> FrozenDeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return FrozenLibInitPlatformUtils.REGISTRY.createDeferredRegister(registryKey, namespace);
	}

	static <T> FrozenDeferredRegister<T> create(Registry<T> registry, String namespace) {
		return create(registry.key(), namespace);
	}

	static Items createItems(String namespace) {
		return FrozenLibInitPlatformUtils.REGISTRY.createDeferredItemsRegister(namespace);
	}

	static Blocks createBlocks(String namespace) {
		return FrozenLibInitPlatformUtils.REGISTRY.createDeferredBlocksRegister(namespace);
	}

	static DataComponents createDataComponents(String namespace) {
		return FrozenLibInitPlatformUtils.REGISTRY.createDeferredDataComponentsRegister(namespace);
	}

	static Entities createEntities(String namespace) {
		return FrozenLibInitPlatformUtils.REGISTRY.createDeferredEntitiesRegister(namespace);
	}

	<I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier);

	<I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also);

	<I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func);

	<I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also);

	<I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier);

	<I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also);

	<I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func);

	<I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, Consumer<I> also);

	void register();

	interface Blocks extends FrozenDeferredRegister<Block> {

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(String name, Supplier<? extends I> supplier);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(String name, Supplier<? extends I> supplier, Consumer<I> also);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(String name, Function<Identifier, ? extends I> func);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier, Consumer<I> also);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func);

		@Override
		<I extends Block> FrozenDeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func, Consumer<I> also);

		<B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties);

		<B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, Consumer<B> also);

		<B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties);

		<B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties, Consumer<B> also);

		<B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func);

		<B extends Block> FrozenDeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, Consumer<B> also);

		default <B extends Block> FrozenDeferredBlock<B> registerBlock(BlockItemId id, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
			return registerBlock(id.block(), func, properties);
		}

		default <B extends Block> FrozenDeferredBlock<B> registerBlock(BlockItemId id, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, Consumer<B> also) {
			return registerBlock(id.block(), func, properties, also);
		}

		default <B extends Block> FrozenDeferredBlock<B> registerBlock(BlockItemId id, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties) {
			return registerBlock(id.block(), func, properties);
		}

		default <B extends Block> FrozenDeferredBlock<B> registerBlock(BlockItemId id, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> properties, Consumer<B> also) {
			return registerBlock(id.block(), func, properties, also);
		}

		default <B extends Block> FrozenDeferredBlock<B> registerBlock(BlockItemId id, Function<BlockBehaviour.Properties, ? extends B> func) {
			return registerBlock(id.block(), func);
		}

		default <B extends Block> FrozenDeferredBlock<B> registerBlock(BlockItemId id, Function<BlockBehaviour.Properties, ? extends B> func, Consumer<B> also) {
			return registerBlock(id.block(), func, also);
		}

		FrozenDeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key, Supplier<BlockBehaviour.Properties> properties);

		FrozenDeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key, UnaryOperator<BlockBehaviour.Properties> properties);

		FrozenDeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key);

		default FrozenDeferredBlock<Block> registerSimpleBlock(BlockItemId id, Supplier<BlockBehaviour.Properties> properties) {
			return registerSimpleBlock(id.block(), properties);
		}

		default FrozenDeferredBlock<Block> registerSimpleBlock(BlockItemId id, UnaryOperator<BlockBehaviour.Properties> properties) {
			return registerSimpleBlock(id.block(), properties);
		}

		default FrozenDeferredBlock<Block> registerSimpleBlock(BlockItemId id) {
			return registerSimpleBlock(id.block());
		}

		default FrozenDeferredBlock<StairBlock> registerLegacyStair(final BlockItemId id, final Supplier<? extends Block> base) {
			return registerBlock(id.block(), p -> new StairBlock(base.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofLegacyCopy(base.get()));
		}

		default FrozenDeferredBlock<StairBlock> registerStair(final BlockItemId id, final Supplier<? extends Block> base) {
			return registerBlock(id.block(), p -> new StairBlock(base.get().defaultBlockState(), p), () -> BlockBehaviour.Properties.ofFullCopy(base.get()));
		}

		default FrozenDeferredBlock<SlabBlock> registerSlab(final BlockItemId id, final Supplier<? extends Block> base) {
			return registerBlock(id.block(), SlabBlock::new, () -> BlockBehaviour.Properties.ofLegacyCopy(base.get()));
		}

		default FrozenDeferredBlock<WallBlock> registerWall(final BlockItemId id, final Supplier<? extends Block> base) {
			return registerBlock(id.block(), WallBlock::new, () -> BlockBehaviour.Properties.ofLegacyCopy(base.get()).forceSolidOn());
		}
	}

	interface Items extends FrozenDeferredRegister<Item> {

		@Override
		<I extends Item> FrozenDeferredItem<I> register(String name, Supplier<? extends I> supplier);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(String name, Supplier<? extends I> supplier, Consumer<I> also);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier, Consumer<I> also);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(String name, Function<Identifier, ? extends I> func);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func);

		@Override
		<I extends Item> FrozenDeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func, Consumer<I> also);

		<I extends Item> FrozenDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties);

		<I extends Item> FrozenDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, UnaryOperator<Item.Properties> properties);

		<I extends Item> FrozenDeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func);

		<I extends Item> FrozenDeferredItem<I> registerItem(ResourceKey<Item> key, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties);

		<I extends Item> FrozenDeferredItem<I> registerItem(ResourceKey<Item> key, Function<Item.Properties, ? extends I> func, UnaryOperator<Item.Properties> properties);

		<I extends Item> FrozenDeferredItem<I> registerItem(ResourceKey<Item> key, Function<Item.Properties, ? extends I> func);

		FrozenDeferredItem<Item> registerSimpleItem(String name, Supplier<Item.Properties> properties);

		FrozenDeferredItem<Item> registerSimpleItem(String name, UnaryOperator<Item.Properties> properties);

		FrozenDeferredItem<Item> registerSimpleItem(String name);

		FrozenDeferredItem<Item> registerSimpleItem(ResourceKey<Item> key, Supplier<Item.Properties> properties);

		FrozenDeferredItem<Item> registerSimpleItem(ResourceKey<Item> key, UnaryOperator<Item.Properties> properties);

		FrozenDeferredItem<Item> registerSimpleItem(ResourceKey<Item> key);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, Supplier<Item.Properties> properties);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, UnaryOperator<Item.Properties> properties);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(BlockItemId name, Supplier<? extends Block> block, Supplier<Item.Properties> properties);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(BlockItemId name, Supplier<? extends Block> block, UnaryOperator<Item.Properties> properties);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(BlockItemId name, Supplier<? extends Block> block);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, Supplier<Item.Properties> properties);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, UnaryOperator<Item.Properties> properties);

		FrozenDeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block);

		default FrozenDeferredItem<SpawnEggItem> registerSpawnEgg(ResourceKey<Item> key, Supplier<EntityType<?>> type) {
			return registerItem(key, SpawnEggItem::new, () -> new Item.Properties().spawnEgg(type.get()));
		}
	}

	interface DataComponents extends FrozenDeferredRegister<DataComponentType<?>> {

		<D> FrozenHolder<DataComponentType<?>, DataComponentType<D>> registerComponent(String name, UnaryOperator<DataComponentType.Builder<D>> builder);
	}

	interface Entities extends FrozenDeferredRegister<EntityType<?>> {

		<E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category);

		<E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, Consumer<EntityType<E>> also);

		<E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder);

		<E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder, Consumer<EntityType<E>> also);
	}
}
