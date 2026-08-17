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
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.platform.api.registry.DeferredBlock;
import net.frozenblock.lib.platform.api.registry.DeferredItem;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

public class NeoDeferredRegister<T> implements DeferredRegister<T> {
	private static final List<DeferredRegister<?>> FAILED_REGISTERS = new ArrayList<>();
	protected final net.neoforged.neoforge.registries.DeferredRegister<T> inner;
	private final Map<DeferredHolder<Object, Object>, Consumer<Object>> consumers = new Object2ObjectLinkedOpenHashMap<>();

	public NeoDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.inner = net.neoforged.neoforge.registries.DeferredRegister.create(registryKey, namespace);
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier, @Nullable Consumer<I> also) {
		final var holder = new NeoDeferredHolder<>(this.inner.register(name, supplier));
		if (also != null) consumers.put((DeferredHolder) holder, (Consumer) also);
		return (DeferredHolder<T, I>) holder;
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also) {
		var holder = new NeoDeferredHolder<>(this.inner.register(name, func));
		if (also != null) consumers.put((DeferredHolder) holder, (Consumer) also);
		return (DeferredHolder<T, I>) holder;
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Supplier<? extends I> supplier, @Nullable Consumer<I> also) {
		var holder = new NeoDeferredHolder<>(this.inner.register(key.identifier().getPath(), supplier));
		if (also != null) consumers.put((DeferredHolder) holder, (Consumer) also);
		return (DeferredHolder<T, I>) holder;
	}

	@Override
	public <I extends T> DeferredHolder<T, I> register(ResourceKey<T> key, Function<Identifier, ? extends I> func, @Nullable Consumer<I> also) {
		var holder = new NeoDeferredHolder<>(this.inner.register(key.identifier().getPath(), func));
		if (also != null) consumers.put((DeferredHolder) holder, (Consumer) also);
		return (DeferredHolder<T, I>) holder;
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

	public static class Blocks extends NeoDeferredRegister<Block> implements DeferredRegister.Blocks {

		public Blocks(String namespace) {
			super(Registries.BLOCK, namespace);
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

	public static class Items extends NeoDeferredRegister<Item> implements DeferredRegister.Items {

		public Items(String namespace) {
			super(Registries.ITEM, namespace);
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

	public static class DataComponents extends NeoDeferredRegister<DataComponentType<?>> implements DeferredRegister.DataComponents {
		public DataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> registryKey, String namespace) {
			super(registryKey, namespace);
		}
	}

	public static class Entities extends NeoDeferredRegister<EntityType<?>> implements DeferredRegister.Entities {
		public Entities(String namespace) {
			super(Registries.ENTITY_TYPE, namespace);
		}
	}

	public static class SoundEvents extends NeoDeferredRegister<SoundEvent> implements DeferredRegister.SoundEvents {
		public SoundEvents(String namespace) {
			super(Registries.SOUND_EVENT, namespace);
		}
	}

	public static class ParticleTypes extends NeoDeferredRegister<ParticleType<?>> implements DeferredRegister.ParticleTypes {
		public ParticleTypes(String namespace) {
			super(Registries.PARTICLE_TYPE, namespace);
		}
	}

	public static class MemoryModuleTypes extends NeoDeferredRegister<MemoryModuleType<?>> implements DeferredRegister.MemoryModuleTypes {
		public MemoryModuleTypes(String namespace) {
			super(Registries.MEMORY_MODULE_TYPE, namespace);
		}
	}

	public static class Activities extends NeoDeferredRegister<Activity> implements DeferredRegister.Activities {
		public Activities(String namespace) {
			super(Registries.ACTIVITY, namespace);
		}
	}

	public static class SensorTypes extends NeoDeferredRegister<SensorType<?>> implements DeferredRegister.SensorTypes {
		public SensorTypes(String namespace) {
			super(Registries.SENSOR_TYPE, namespace);
		}
	}
}
