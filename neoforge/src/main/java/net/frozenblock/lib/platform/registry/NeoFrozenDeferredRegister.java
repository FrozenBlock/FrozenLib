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

package net.frozenblock.lib.platform.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredBlock;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredItem;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
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
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

public class NeoFrozenDeferredRegister<T> implements FrozenDeferredRegister<T> {
	private static final List<FrozenDeferredRegister<?>> FAILED_REGISTERS = new ArrayList<>();
	protected final DeferredRegister<T> inner;
	private final Map<FrozenHolder<Object, Object>, Consumer<Object>> consumers = new Object2ObjectLinkedOpenHashMap<>();

	public NeoFrozenDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.inner = DeferredRegister.create(registryKey, namespace);
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier) {
		return new NeoFrozenHolder<>(this.inner.register(name, supplier));
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Supplier<? extends I> supplier, Consumer<I> also) {
		final var holder = new NeoFrozenHolder<>(this.inner.register(name, supplier));
		if (also != null) consumers.put((FrozenHolder) holder, (Consumer) also);
		return (FrozenHolder<T, I>) holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
		return new NeoFrozenHolder<>(this.inner.register(name, func));
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(String name, Function<Identifier, ? extends I> func, Consumer<I> also) {
		var holder = new NeoFrozenHolder<>(this.inner.register(name, func));
		if (also != null) consumers.put((FrozenHolder) holder, (Consumer) also);
		return (FrozenHolder<T, I>) holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier) {
		return new NeoFrozenHolder<>(this.inner.register(key.identifier().getPath(), supplier));
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, Consumer<I> also) {
		var holder = new NeoFrozenHolder<>(this.inner.register(key.identifier().getPath(), supplier));
		if (also != null) consumers.put((FrozenHolder) holder, (Consumer) also);
		return (FrozenHolder<T, I>) holder;
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func) {
		return new NeoFrozenHolder<>(this.inner.register(key.identifier().getPath(), func));
	}

	@Override
	public <I extends T> FrozenHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, Consumer<I> also) {
		var holder = new NeoFrozenHolder<>(this.inner.register(key.identifier().getPath(), func));
		if (also != null) consumers.put((FrozenHolder) holder, (Consumer) also);
		return (FrozenHolder<T, I>) holder;
	}

	@Override
	public void register() {
		try	{
			final var bus = ModLoadingContext.get().getActiveContainer().getEventBus();
			this.inner.register(bus);
			bus.addListener(this::runCallbacks);
		} catch (Exception e) {
			if (!FAILED_REGISTERS.contains(this)) {
				FAILED_REGISTERS.add(this);
				FrozenLibLogUtils.logError(
					"Failed to register FrozenLib Deferred Register for registry" + this.inner.getRegistry() + ", postponing register event."
					+ "\nThis is NOT acceptable implementation, please try to alleviate this issue the best you can if viable.",
					ModLoader.isDevelopmentEnvironment()
				);
			}
		}
	}

	@Override
	public String namespace() {
		return this.inner.getNamespace();
	}

	public static void tryRegisterFailedRegisters() {
		FAILED_REGISTERS.removeIf(register -> {
			register.register();
			return true;
		});
	}

	private void runCallbacks(RegisterEvent event) {
		if (!event.getRegistryKey().equals(this.inner.getRegistryKey())) return;

		for (var consumer : consumers.entrySet()) consumer.getValue().accept(consumer.getKey().get());
	}

	public static class Blocks extends NeoFrozenDeferredRegister<Block> implements FrozenDeferredRegister.Blocks {

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

	public static class Items extends NeoFrozenDeferredRegister<Item> implements FrozenDeferredRegister.Items {

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

	public static class DataComponents extends NeoFrozenDeferredRegister<DataComponentType<?>> implements FrozenDeferredRegister.DataComponents {

		public DataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> registryKey, String namespace) {
			super(registryKey, namespace);
		}

		@Override
		public <D> FrozenHolder<DataComponentType<?>, DataComponentType<D>> registerComponent(String name, UnaryOperator<DataComponentType.Builder<D>> builder) {
			return register(name, () -> builder.apply(DataComponentType.builder()).build());
		}
	}

	public static class Entities extends NeoFrozenDeferredRegister<EntityType<?>> implements FrozenDeferredRegister.Entities {

		public Entities(String namespace) {
			super(Registries.ENTITY_TYPE, namespace);
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category) {
			var id = Identifier.fromNamespaceAndPath(this.inner.getNamespace(), name);
			return register(name, () -> EntityType.Builder.of(factory, category).build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, Consumer<EntityType<E>> also) {
			var id = Identifier.fromNamespaceAndPath(this.inner.getNamespace(), name);
			return register(name, () -> EntityType.Builder.of(factory, category).build(ResourceKey.create(Registries.ENTITY_TYPE, id)), also);
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder) {
			return register(name, () -> {
				var id = Identifier.fromNamespaceAndPath(this.inner.getNamespace(), name);
				var b = EntityType.Builder.of(factory, category);
				return builder.apply(b).build(ResourceKey.create(Registries.ENTITY_TYPE, id));
			});
		}

		@Override
		public <E extends Entity> FrozenHolder<EntityType<?>, EntityType<E>> registerEntityType(String name, EntityType.EntityFactory<E> factory, MobCategory category, UnaryOperator<EntityType.Builder<E>> builder, Consumer<EntityType<E>> also) {
			return register(name, () -> {
				var id = Identifier.fromNamespaceAndPath(this.inner.getNamespace(), name);
				var b = EntityType.Builder.of(factory, category);
				return builder.apply(b).build(ResourceKey.create(Registries.ENTITY_TYPE, id));
			}, also);
		}
	}
}
