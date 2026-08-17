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

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.frozenblock.lib.platform.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

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
public interface DeferredRegister<T> {

	static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return RegistryHelper.createDeferredRegister(registryKey, namespace);
	}

	static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) {
		return create(registry.key(), namespace);
	}

	static Items createItems(String namespace) {
		return RegistryHelper.createDeferredItemsRegister(namespace);
	}

	static Blocks createBlocks(String namespace) {
		return RegistryHelper.createDeferredBlocksRegister(namespace);
	}

	static DataComponents createDataComponents(String namespace) {
		return RegistryHelper.createDeferredDataComponentsRegister(namespace);
	}

	static Entities createEntities(String namespace) {
		return RegistryHelper.createDeferredEntitiesRegister(namespace);
	}

	static SoundEvents createSoundEvents(String namespace) {
		return RegistryHelper.createDeferredSoundEventsRegister(namespace);
	}

	static ParticleTypes createParticleTypes(String namespace) {
		return RegistryHelper.createDeferredParticleTypesRegister(namespace);
	}

	static MemoryModuleTypes createMemoryModuleTypes(String namespace) {
		return RegistryHelper.createDeferredMemoryModuleTypesRegister(namespace);
	}

	static Activities createActivities(String namespace) {
		return RegistryHelper.createDeferredActivitiesRegister(namespace);
	}

	<I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier, @Nullable Consumer<I> also);

	default <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier) {
		return  register(name, supplier, null);
	}

	<I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also);

	default <I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
		return this.register(name, func, null);
	}

	<I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, @Nullable Consumer<I> also);

	default <I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier) {
		return this.register(key, supplier, null);
	}

	<I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also);

	default <I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func) {
		return this.register(key, func, null);
	}

	void register();

	String namespace();

	interface Blocks extends DeferredRegister<Block> {

		@Override
		<I extends Block> DeferredBlock<I> register(String name, Supplier<? extends I> supplier, @Nullable Consumer<I> also);

		@Override
		default <I extends Block> DeferredBlock<I> register(String name, Supplier<? extends I> supplier) {
			return this.register(name, supplier, null);
		}

		@Override
		<I extends Block> DeferredBlock<I> register(String name, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also);

		@Override
		default <I extends Block> DeferredBlock<I> register(String name, Function<Identifier, ? extends I> func) {
			return this.register(name, func, null);
		}

		@Override
		<I extends Block> DeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier, @Nullable Consumer<I> also);

		@Override
		default <I extends Block> DeferredBlock<I> register(ResourceKey<Block> key, Supplier<? extends I> supplier) {
			return this.register(key, supplier, null);
		}

		default <I extends Block> DeferredBlock<I> register(BlockItemId key, Supplier<? extends I> supplier, @Nullable Consumer<I> also) {
			return this.register(key.block(), supplier, also);
		}

		default <I extends Block> DeferredBlock<I> register(BlockItemId key, Supplier<? extends I> supplier) {
			return this.register(key, supplier, null);
		}

		@Override
		<I extends Block> DeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also);

		@Override
		default <I extends Block> DeferredBlock<I> register(ResourceKey<Block> key, Function<Identifier, ? extends I> func) {
			return this.register(key, func, null);
		}

		default <I extends Block> DeferredBlock<I> register(BlockItemId key, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also) {
			return this.register(key.block(), func, also);
		}

		default <I extends Block> DeferredBlock<I> register(BlockItemId key, Function<Identifier, ? extends I> func) {
			return this.register(key, func, null);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<BlockBehaviour.Properties> properties,
			@Nullable Consumer<B> also
		) {
			return new DeferredBlock<>(this.register(key, () -> func.apply(properties.get().setId(key)), also));
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<BlockBehaviour.Properties> properties
		) {
			return this.registerBlock(key, func, properties, null);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<BlockBehaviour.Properties> properties,
			@Nullable Consumer<B> also
		) {
			return this.registerBlock(key.block(), func, properties, also);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(BlockItemId key, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
			return this.registerBlock(key, func, properties, null);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			UnaryOperator<BlockBehaviour.Properties> properties,
			@Nullable Consumer<B> also
		) {
			return this.registerBlock(key, func, () -> properties.apply(BlockBehaviour.Properties.of()), also);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			UnaryOperator<BlockBehaviour.Properties> properties
		) {
			return this.registerBlock(key, func, properties, null);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			UnaryOperator<BlockBehaviour.Properties> properties,
			@Nullable Consumer<B> also
		) {
			return this.registerBlock(key.block(), func, properties, also);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			UnaryOperator<BlockBehaviour.Properties> properties
		) {
			return this.registerBlock(key, func, properties, null);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func, @Nullable Consumer<B> also) {
			return this.registerBlock(key, func, BlockBehaviour.Properties::of, also);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(ResourceKey<Block> key, Function<BlockBehaviour.Properties, ? extends B> func) {
			return this.registerBlock(key, func, BlockBehaviour.Properties::of);
		}

		default <B extends Block> DeferredBlock<B> registerBlock(BlockItemId key, Function<BlockBehaviour.Properties, ? extends B> func) {
			return this.registerBlock(key.block(), func);
		}

		default DeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key, Supplier<BlockBehaviour.Properties> properties) {
			return this.registerBlock(key, Block::new, properties);
		}

		default DeferredBlock<Block> registerSimpleBlock(BlockItemId key, Supplier<BlockBehaviour.Properties> properties) {
			return this.registerSimpleBlock(key.block(), properties);
		}

		default DeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key, UnaryOperator<BlockBehaviour.Properties> properties) {
			return this.registerBlock(key, Block::new, properties);
		}

		default DeferredBlock<Block> registerSimpleBlock(BlockItemId key, UnaryOperator<BlockBehaviour.Properties> properties) {
			return this.registerSimpleBlock(key.block(), properties);
		}

		default DeferredBlock<Block> registerSimpleBlock(ResourceKey<Block> key) {
			return this.registerBlock(key, Block::new);
		}

		default DeferredBlock<Block> registerSimpleBlock(BlockItemId key) {
			return this.registerSimpleBlock(key.block());
		}

		default DeferredBlock<DropExperienceBlock> registerDropExperienceBlock(ResourceKey<Block> key, IntProvider xpRange, Supplier<BlockBehaviour.Properties> properties) {
			return this.registerBlock(key, props -> new DropExperienceBlock(xpRange, props), properties);
		}

		default DeferredBlock<DropExperienceBlock> registerDropExperienceBlock(BlockItemId key, IntProvider xpRange, Supplier<BlockBehaviour.Properties> properties) {
			return this.registerDropExperienceBlock(key.block(), xpRange, properties);
		}

		default DeferredBlock<StairBlock> registerLegacyStair(BlockItemId id, Supplier<? extends Block> base) {
			return this.registerLegacyCopy(id, p -> new StairBlock(base.get().defaultBlockState(), p), base);
		}

		default DeferredBlock<StairBlock> registerStair(BlockItemId id, Supplier<? extends Block> base) {
			return this.registerFullCopy(id.block(), p -> new StairBlock(base.get().defaultBlockState(), p), base);
		}

		default DeferredBlock<SlabBlock> registerSlab(BlockItemId id, Supplier<? extends Block> base) {
			return this.registerLegacyCopy(id.block(), SlabBlock::new, base);
		}

		default DeferredBlock<WallBlock> registerWall(BlockItemId id, Supplier<? extends Block> base) {
			return this.registerLegacyCopy(id.block(), WallBlock::new, base, BlockBehaviour.Properties::forceSolidOn);
		}

		default DeferredBlock<FenceBlock> registerFence(BlockItemId id, Supplier<? extends Block> base) {
			return this.registerLegacyCopy(id.block(), FenceBlock::new, base);
		}

		default DeferredBlock<FenceGateBlock> registerFenceGate(BlockItemId id, WoodType woodType, Supplier<? extends Block> base) {
			return this.registerLegacyCopy(id.block(), properties -> new FenceGateBlock(woodType, properties), base, BlockBehaviour.Properties::forceSolidOn);
		}

		default DeferredBlock<PressurePlateBlock> registerPressurePlate(BlockItemId id, BlockSetType blockSetType, Supplier<? extends Block> base) {
			return this.registerLegacyCopy(
				id.block(),
				properties -> new PressurePlateBlock(blockSetType, properties),
				base,
				properties -> properties.forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY)
			);
		}

		default DeferredBlock<ButtonBlock> registerButton(BlockItemId id, BlockSetType blockSetType, int ticksToStayPressed) {
			return this.registerBlock(
				id.block(),
				properties -> new ButtonBlock(blockSetType, ticksToStayPressed, properties),
				net.minecraft.world.level.block.Blocks::buttonProperties
			);
		}

		default DeferredBlock<ButtonBlock> registerWoodenButton(BlockItemId id, BlockSetType blockSetType) {
			return this.registerButton(id, blockSetType, 30);
		}

		default DeferredBlock<ButtonBlock> registerStoneButton(BlockItemId id, BlockSetType blockSetType) {
			return this.registerButton(id, blockSetType, 20);
		}

		private DeferredBlock<DoorBlock> registerDoor(
			BlockItemId id,
			BlockSetType blockSetType,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> propertiesOp
		) {
			return this.registerLegacyCopy(
				id.block(),
				properties -> new DoorBlock(blockSetType, properties),
				base,
				properties -> propertiesOp.apply(properties.noOcclusion().pushReaction(PushReaction.DESTROY))
			);
		}

		default DeferredBlock<DoorBlock> registerWoodenDoor(BlockItemId id, BlockSetType blockSetType, Supplier<? extends Block> base) {
			return this.registerDoor(id, blockSetType, base, properties -> properties.strength(3F));
		}

		default DeferredBlock<DoorBlock> registerMetalDoor(
			BlockItemId id,
			BlockSetType blockSetType,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> propertiesOp
		) {
			return this.registerBlock(
				id.block(),
				properties -> new DoorBlock(blockSetType, properties),
				() -> propertiesOp.apply(
					BlockBehaviour.Properties.of()
						.mapColor(base.get().defaultMapColor())
						.strength(base.get().properties().destroyTime)
						.noOcclusion()
						.pushReaction(PushReaction.DESTROY)
				)
			);
		}

		default DeferredBlock<DoorBlock> registerMetalDoor(BlockItemId id, BlockSetType blockSetType, Supplier<? extends Block> base) {
			return this.registerMetalDoor(id, blockSetType, base, properties -> properties);
		}

		private DeferredBlock<TrapDoorBlock> registerTrapDoor(
			BlockItemId id,
			BlockSetType blockSetType,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> propertiesOp
		) {
			return this.registerLegacyCopy(
				id.block(),
				properties -> new TrapDoorBlock(blockSetType, properties),
				base,
				properties -> propertiesOp.apply(properties.noOcclusion().isValidSpawn(net.minecraft.world.level.block.Blocks::never))
			);
		}

		default DeferredBlock<TrapDoorBlock> registerWoodenTrapDoor(BlockItemId id, BlockSetType blockSetType, Supplier<? extends Block> base) {
			return this.registerTrapDoor(id, blockSetType, base, properties -> properties.strength(3F));
		}

		default DeferredBlock<TrapDoorBlock> registerMetalTrapDoor(
			BlockItemId id,
			BlockSetType blockSetType,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> propertiesOp
		) {
			return this.registerBlock(
				id.block(),
				properties -> new TrapDoorBlock(blockSetType, properties),
				() -> propertiesOp.apply(
					BlockBehaviour.Properties.of()
						.mapColor(base.get().defaultMapColor())
						.requiresCorrectToolForDrops()
						.strength(base.get().properties().destroyTime)
						.noOcclusion()
						.isValidSpawn(net.minecraft.world.level.block.Blocks::never)
				)
			);
		}

		default DeferredBlock<TrapDoorBlock> registerMetalTrapDoor(BlockItemId id, BlockSetType blockSetType, Supplier<? extends Block> base) {
			return this.registerMetalTrapDoor(id, blockSetType, base, properties -> properties);
		}

		// FULL COPY
		default <B extends Block> DeferredBlock<B> registerFullCopy(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> properties
		) {
			return this.registerBlock(key, func, () -> properties.apply(BlockBehaviour.Properties.ofFullCopy(base.get())), null);
		}

		default <B extends Block> DeferredBlock<B> registerFullCopy(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base
		) {
			return this.registerFullCopy(key, func, base, properties -> properties);
		}

		default <B extends Block> DeferredBlock<B> registerFullCopy(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> properties
		) {
			return this.registerFullCopy(key.block(), func, base, properties);
		}

		default <B extends Block> DeferredBlock<B> registerFullCopy(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base
		) {
			return this.registerFullCopy(key.block(), func, base);
		}

		default DeferredBlock<Block> registerSimpleFullCopy(ResourceKey<Block> key, Supplier<? extends Block> base, UnaryOperator<BlockBehaviour.Properties> properties) {
			return this.registerFullCopy(key, Block::new, base, properties);
		}

		default DeferredBlock<Block> registerSimpleFullCopy(ResourceKey<Block> key, Supplier<? extends Block> base) {
			return this.registerSimpleFullCopy(key, base, properties -> properties);
		}

		default DeferredBlock<Block> registerSimpleFullCopy(BlockItemId key, Supplier<? extends Block> base, UnaryOperator<BlockBehaviour.Properties> properties) {
			return this.registerSimpleFullCopy(key.block(), base, properties);
		}

		default DeferredBlock<Block> registerSimpleFullCopy(BlockItemId key, Supplier<? extends Block> base) {
			return this.registerSimpleFullCopy(key.block(), base);
		}

		// LEGACY COPY
		default <B extends Block> DeferredBlock<B> registerLegacyCopy(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> properties
		) {
			return this.registerBlock(key, func, () -> properties.apply(BlockBehaviour.Properties.ofLegacyCopy(base.get())), null);
		}

		default <B extends Block> DeferredBlock<B> registerLegacyCopy(
			ResourceKey<Block> key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base
		) {
			return this.registerLegacyCopy(key, func, base, properties -> properties);
		}

		default <B extends Block> DeferredBlock<B> registerLegacyCopy(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base,
			UnaryOperator<BlockBehaviour.Properties> properties
		) {
			return this.registerLegacyCopy(key.block(), func, base, properties);
		}

		default <B extends Block> DeferredBlock<B> registerLegacyCopy(
			BlockItemId key,
			Function<BlockBehaviour.Properties, ? extends B> func,
			Supplier<? extends Block> base
		) {
			return this.registerLegacyCopy(key.block(), func, base);
		}

		default DeferredBlock<Block> registerSimpleLegacyCopy(ResourceKey<Block> key, Supplier<? extends Block> base, UnaryOperator<BlockBehaviour.Properties> properties) {
			return this.registerLegacyCopy(key, Block::new, base, properties);
		}

		default DeferredBlock<Block> registerSimpleLegacyCopy(ResourceKey<Block> key, Supplier<? extends Block> base) {
			return this.registerSimpleLegacyCopy(key, base, properties -> properties);
		}

		default DeferredBlock<Block> registerSimpleLegacyCopy(BlockItemId key, Supplier<? extends Block> base, UnaryOperator<BlockBehaviour.Properties> properties) {
			return this.registerSimpleLegacyCopy(key.block(), base, properties);
		}

		default DeferredBlock<Block> registerSimpleLegacyCopy(BlockItemId key, Supplier<? extends Block> base) {
			return this.registerSimpleLegacyCopy(key.block(), base);
		}

		// COPPER
		default <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper, Id> WeatheringCopperCollection<DeferredBlock<? extends Block>> registerWeatheringCopperCollection(
			WeatheringCopperCollection<Id> ids,
			Function4<Blocks, Id, Function<BlockBehaviour.Properties, Block>, Supplier<BlockBehaviour.Properties>, DeferredBlock<?>> register,
			BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WaxedBlock> waxedBlockFactory,
			BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory,
			Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier
		) {
			return ids.apply(
				weatheringIds -> WeatheringCopperCollection.zipMap(
					WeatheringCopperCollection.STATES,
					weatheringIds,
					(state, id) -> register.apply(this, id, p -> weatheringFactory.apply(state, p), () -> propertiesSupplier.apply(state))
				),
				waxedIds -> WeatheringCopperCollection.zipMap(
					WeatheringCopperCollection.STATES,
					waxedIds,
					(state, id) -> register.apply(this, id, p -> waxedBlockFactory.apply(state, p), () -> propertiesSupplier.apply(state))
				)
			);
		}
	}

	interface Items extends DeferredRegister<Item> {

		@Override
		<I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier, @Nullable Consumer<I> also);

		@Override
		default <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier) {
			return this.register(name, supplier, null);
		}

		@Override
		<I extends Item> DeferredItem<I> register(String name, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also);

		@Override
		default <I extends Item> DeferredItem<I> register(String name, Function<Identifier, ? extends I> func) {
			return this.register(name, func, null);
		}

		@Override
		<I extends Item> DeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier, @Nullable Consumer<I> also);

		@Override
		default <I extends Item> DeferredItem<I> register(ResourceKey<Item> key, Supplier<? extends I> supplier) {
			return this.register(key, supplier, null);
		}

		@Override
		<I extends Item> DeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also);

		@Override
		default <I extends Item> DeferredItem<I> register(ResourceKey<Item> key, Function<Identifier, ? extends I> func) {
			return this.register(key, func, null);
		}

		default <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
			final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.namespace(), name));
			return new DeferredItem<>(this.register(name, () -> func.apply(properties.get().setId(key))));
		}

		default <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, UnaryOperator<Item.Properties> properties) {
			return this.registerItem(name, func, () -> properties.apply(new Item.Properties()));
		}

		default <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
			return this.registerItem(name, func, Item.Properties::new);
		}

		default <I extends Item> DeferredItem<I> registerItem(ResourceKey<Item> key, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
			return this.registerItem(key.identifier().getPath(), func, properties);
		}

		default <I extends Item> DeferredItem<I> registerItem(ResourceKey<Item> key, Function<Item.Properties, ? extends I> func, UnaryOperator<Item.Properties> properties) {
			return this.registerItem(key.identifier().getPath(), func, properties);
		}

		default <I extends Item> DeferredItem<I> registerItem(ResourceKey<Item> key, Function<Item.Properties, ? extends I> func) {
			return this.registerItem(key.identifier().getPath(), func);
		}

		default DeferredItem<Item> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
			return this.registerItem(name, Item::new, properties);
		}

		default DeferredItem<Item> registerSimpleItem(String name, UnaryOperator<Item.Properties> properties) {
			return this.registerItem(name, Item::new, properties);
		}

		default DeferredItem<Item> registerSimpleItem(String name) {
			return this.registerItem(name, Item::new);
		}

		default DeferredItem<Item> registerSimpleItem(ResourceKey<Item> key, Supplier<Item.Properties> properties) {
			return this.registerItem(key, Item::new, properties);
		}

		default DeferredItem<Item> registerSimpleItem(ResourceKey<Item> key, UnaryOperator<Item.Properties> properties) {
			return this.registerItem(key, Item::new, properties);
		}

		default DeferredItem<Item> registerSimpleItem(ResourceKey<Item> key) {
			return this.registerItem(key, Item::new);
		}

		default <I extends BlockItem> DeferredItem<I> registerBlockItem(
			String name,
			BiFunction<Item.Properties, Block, ? extends I> func,
			Supplier<? extends Block> block,
			Supplier<Item.Properties> properties
		) {
			return this.registerItem(name, props -> func.apply(props, block.get()), () -> properties.get().useBlockDescriptionPrefix());
		}

		default <I extends BlockItem> DeferredItem<I> registerBlockItem(
			BlockItemId key,
			BiFunction<Item.Properties, Block, ? extends I> func,
			Supplier<? extends Block> block,
			Supplier<Item.Properties> properties
		) {
			return this.registerItem(key.item(), props -> func.apply(props, block.get()), () -> properties.get().useBlockDescriptionPrefix());
		}

		default <I extends BlockItem> DeferredItem<I> registerBlockItem(
			String name,
			BiFunction<Item.Properties, Block, ? extends I> func,
			Supplier<? extends Block> block,
			UnaryOperator<Item.Properties> properties
		) {
			return this.registerBlockItem(name, func, block, () -> properties.apply(new Item.Properties()));
		}

		default <I extends BlockItem> DeferredItem<I> registerBlockItem(
			BlockItemId key,
			BiFunction<Item.Properties, Block, ? extends I> func,
			Supplier<? extends Block> block,
			UnaryOperator<Item.Properties> properties
		) {
			return this.registerBlockItem(key, func, block, () -> properties.apply(new Item.Properties()));
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> base, Supplier<Item.Properties> properties) {
			return this.registerBlockItem(name, (props, block) -> new BlockItem(block, props), base, properties);
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(BlockItemId key, Supplier<? extends Block> base, Supplier<Item.Properties> properties) {
			return this.registerBlockItem(key, (props, block) -> new BlockItem(block, props), base, properties);
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, UnaryOperator<Item.Properties> properties) {
			return this.registerSimpleBlockItem(name, block, () -> properties.apply(new Item.Properties()));
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(BlockItemId key, Supplier<? extends Block> block, UnaryOperator<Item.Properties> properties) {
			return this.registerSimpleBlockItem(key, block, () -> properties.apply(new Item.Properties()));
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block) {
			return this.registerSimpleBlockItem(name, block, Item.Properties::new);
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(BlockItemId key, Supplier<? extends Block> block) {
			return this.registerSimpleBlockItem(key, block, Item.Properties::new);
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, Supplier<Item.Properties> properties) {
			return this.registerSimpleBlockItem(block.unwrapKey().orElseThrow().identifier().getPath(), block::value, properties);
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, UnaryOperator<Item.Properties> properties) {
			return this.registerSimpleBlockItem(block, () -> properties.apply(new Item.Properties()));
		}

		default DeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block) {
			return this.registerSimpleBlockItem(block, Item.Properties::new);
		}

		default DeferredItem<Item> registerMusicDisc(ResourceKey<Item> key, ResourceKey<JukeboxSong> song) {
			return this.registerSimpleItem(key, () -> new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(song));
		}

		default DeferredItem<SpawnEggItem> registerSpawnEgg(ResourceKey<Item> key, Supplier<EntityType<?>> type) {
			return this.registerItem(key, SpawnEggItem::new, () -> new Item.Properties().spawnEgg(type.get()));
		}

		default DeferredItem<SpawnEggItem> registerSpawnEgg(ResourceKey<Item> key, DeferredEntityType<?> type) {
			return this.registerSpawnEgg(key, type::get);
		}
	}

	interface DataComponents extends DeferredRegister<DataComponentType<?>> {
		default <T> DeferredDataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
			return new DeferredDataComponentType<>(register(name, () -> builder.apply(DataComponentType.builder()).build()));
		}
	}

	interface Entities extends DeferredRegister<EntityType<?>> {

		default <E extends Entity> DeferredEntityType<E> register(
			String name,
			EntityType.EntityFactory<E> factory,
			MobCategory category,
			UnaryOperator<EntityType.Builder<E>> builder,
			@Nullable Consumer<EntityType<E>> also
		) {
			return new DeferredEntityType<>(
				this.register(
					name,
					() -> {
						final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(this.namespace(), name));
						final EntityType.Builder<E> b = EntityType.Builder.of(factory, category);
						return builder.apply(b).build(key);
					},
					also
				)
			);
		}

		default <E extends Entity> DeferredEntityType<E> register(
			String name,
			EntityType.EntityFactory<E> factory,
			MobCategory category,
			@Nullable Consumer<EntityType<E>> also
		) {
			return this.register(name, factory, category, type -> type, also);
		}

		default <E extends Entity> DeferredEntityType<E> register(String name, EntityType.EntityFactory<E> factory, MobCategory category) {
			return this.register(name, factory, category, type -> {});
		}

		default <E extends Entity> DeferredEntityType<E> register(
			String name,
			EntityType.EntityFactory<E> factory,
			MobCategory category,
			UnaryOperator<EntityType.Builder<E>> builder
		) {
			return this.register(name, factory, category, builder, null);
		}
	}

	interface SoundEvents extends DeferredRegister<SoundEvent> {

		default DeferredSoundEvent register(String name) {
			return new DeferredSoundEvent(this.register(name, () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(this.namespace(), name))));
		}

		default DeferredSoundEvent register(String name, float range) {
			return new DeferredSoundEvent(this.register(name, () -> SoundEvent.createFixedRangeEvent(Identifier.fromNamespaceAndPath(this.namespace(), name), range)));
		}
	}

	interface ParticleTypes extends DeferredRegister<ParticleType<?>> {

		default DeferredSimpleParticleType register(String name, boolean overrideLimiter) {
			return new DeferredSimpleParticleType(this.register(name, () -> ParticleTypeHelper.simple(overrideLimiter)));
		}

		default DeferredSimpleParticleType register(String name) {
			return this.register(name, false);
		}

		/**
		 * @see ParticleTypeHelper
		 */
		default <T extends ParticleOptions> DeferredParticleType<T> register(String name, ParticleType<T> type) {
			return new DeferredParticleType<>(this.register(name, () -> type));
		}
	}

	interface MemoryModuleTypes extends DeferredRegister<MemoryModuleType<?>> {

		default <U> DeferredMemoryModuleType<U> register(String name) {
			return new DeferredMemoryModuleType<>(this.register(name, () -> new MemoryModuleType<>(Optional.empty())));
		}

		default <U> DeferredMemoryModuleType<U> register(String name, Codec<U> codec) {
			return new DeferredMemoryModuleType<>(this.register(name, () -> new MemoryModuleType<>(Optional.of(codec))));
		}
	}

	interface Activities extends DeferredRegister<Activity> {
		default DeferredActivity register(String name) {
			return new DeferredActivity(this.register(name, () -> new Activity(this.namespace() + "_" + name)));
		}
	}
}
