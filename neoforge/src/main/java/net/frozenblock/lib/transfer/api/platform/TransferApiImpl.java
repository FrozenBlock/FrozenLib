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

package net.frozenblock.lib.transfer.api.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.frozenblock.lib.transfer.api.FluidContainerItem;
import net.frozenblock.lib.transfer.api.FluidHandler;
import net.frozenblock.lib.transfer.api.FluidHandlerCache;
import net.frozenblock.lib.transfer.api.FluidStack;
import net.frozenblock.lib.transfer.api.FluidVariant;
import net.frozenblock.lib.transfer.api.ItemHandler;
import net.frozenblock.lib.transfer.api.ItemHandlerCache;
import net.frozenblock.lib.transfer.api.TransferApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public final class TransferApiImpl {
	private static final List<ItemRegistration<?>> ITEM_REGISTRATIONS = new ArrayList<>();
	private static final List<FluidRegistration<?>> FLUID_REGISTRATIONS = new ArrayList<>();

	private TransferApiImpl() {
	}

	public static boolean hasItemStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		return level.getCapability(Capabilities.Item.BLOCK, pos, direction) != null;
	}

	public static int insertItem(Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, boolean simulate) {
		ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction);
		if (handler == null || stack.isEmpty()) return 0;

		ItemResource resource = ItemResource.of(stack);

		try (Transaction transaction = Transaction.open(null)) {
			int inserted = handler.insert(resource, stack.getCount(), transaction);
			if (!simulate) transaction.commit();
			return inserted;
		}
	}

	public static ItemStack extractItem(
		Level level,
		BlockPos pos,
		@Nullable Direction direction,
		@Nullable Predicate<ItemStack> filter,
		int maxAmount,
		boolean simulate
	) {
		ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction);
		if (handler == null || maxAmount <= 0) return ItemStack.EMPTY;

		Predicate<ItemResource> resourceFilter = filter == null ? resource -> true : resource -> filter.test(resource.toStack());

		try (Transaction transaction = Transaction.open(null)) {
			ResourceStack<ItemResource> extracted = ResourceHandlerUtil.extractFirst(handler, resourceFilter, maxAmount, transaction);
			if (extracted == null) return ItemStack.EMPTY;

			if (!simulate) transaction.commit();
			return extracted.resource().toStack(extracted.amount());
		}
	}

	public static int moveItems(
		Level level,
		BlockPos fromPos,
		@Nullable Direction fromDirection,
		BlockPos toPos,
		@Nullable Direction toDirection,
		@Nullable Predicate<ItemStack> filter,
		int maxAmount
	) {
		ResourceHandler<ItemResource> from = level.getCapability(Capabilities.Item.BLOCK, fromPos, fromDirection);
		ResourceHandler<ItemResource> to = level.getCapability(Capabilities.Item.BLOCK, toPos, toDirection);
		if (from == null || to == null || maxAmount <= 0) return 0;

		Predicate<ItemResource> resourceFilter = filter == null ? resource -> true : resource -> filter.test(resource.toStack());
		return ResourceHandlerUtil.move(from, to, resourceFilter, maxAmount, null);
	}

	public static boolean supportsItemInsertion(Level level, BlockPos pos, @Nullable Direction direction) {
		ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction);
		return handler != null && hasAnyCapacity(handler, ItemResource.EMPTY);
	}

	public static boolean supportsItemExtraction(Level level, BlockPos pos, @Nullable Direction direction) {
		ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction);
		if (handler == null) return false;

		try (Transaction transaction = Transaction.open(null)) {
			return ResourceHandlerUtil.extractFirst(handler, resource -> true, 1, transaction) != null;
		}
	}

	public static boolean hasFluidStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		return level.getCapability(Capabilities.Fluid.BLOCK, pos, direction) != null;
	}

	public static int insertFluid(Level level, BlockPos pos, @Nullable Direction direction, FluidVariant variant, int maxAmount, boolean simulate) {
		ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction);
		if (handler == null || variant.isBlank() || maxAmount <= 0) return 0;

		FluidResource resource = toNeoForge(variant);

		try (Transaction transaction = Transaction.open(null)) {
			int inserted = handler.insert(resource, maxAmount, transaction);
			if (!simulate) transaction.commit();
			return inserted;
		}
	}

	public static FluidStack extractFluid(
		Level level,
		BlockPos pos,
		@Nullable Direction direction,
		@Nullable Predicate<FluidVariant> filter,
		int maxAmount,
		boolean simulate
	) {
		ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction);
		if (handler == null || maxAmount <= 0) return FluidStack.EMPTY;

		Predicate<FluidResource> resourceFilter = filter == null ? resource -> true : resource -> filter.test(fromNeoForge(resource));

		try (Transaction transaction = Transaction.open(null)) {
			ResourceStack<FluidResource> extracted = ResourceHandlerUtil.extractFirst(handler, resourceFilter, maxAmount, transaction);
			if (extracted == null) return FluidStack.EMPTY;

			if (!simulate) transaction.commit();
			return new FluidStack(fromNeoForge(extracted.resource()), extracted.amount());
		}
	}

	public static int moveFluids(
		Level level,
		BlockPos fromPos,
		@Nullable Direction fromDirection,
		BlockPos toPos,
		@Nullable Direction toDirection,
		@Nullable Predicate<FluidVariant> filter,
		int maxAmount
	) {
		ResourceHandler<FluidResource> from = level.getCapability(Capabilities.Fluid.BLOCK, fromPos, fromDirection);
		ResourceHandler<FluidResource> to = level.getCapability(Capabilities.Fluid.BLOCK, toPos, toDirection);
		if (from == null || to == null || maxAmount <= 0) return 0;

		Predicate<FluidResource> resourceFilter = filter == null ? resource -> true : resource -> filter.test(fromNeoForge(resource));
		return ResourceHandlerUtil.move(from, to, resourceFilter, maxAmount, null);
	}

	public static boolean supportsFluidInsertion(Level level, BlockPos pos, @Nullable Direction direction) {
		ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction);
		return handler != null && hasAnyCapacity(handler, FluidResource.EMPTY);
	}

	public static boolean supportsFluidExtraction(Level level, BlockPos pos, @Nullable Direction direction) {
		ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction);
		if (handler == null) return false;

		try (Transaction transaction = Transaction.open(null)) {
			return ResourceHandlerUtil.extractFirst(handler, resource -> true, 1, transaction) != null;
		}
	}

	private static <T extends Resource> boolean hasAnyCapacity(ResourceHandler<T> handler, T emptyResource) {
		for (int index = 0; index < handler.size(); index++) {
			if (handler.getCapacityAsLong(index, emptyResource) > 0) return true;
		}
		return false;
	}

	static FluidResource toNeoForge(FluidVariant variant) {
		return FluidResource.of(variant.fluid(), variant.components());
	}

	static FluidVariant fromNeoForge(FluidResource resource) {
		return FluidVariant.of(resource.getFluid(), resource.getComponentsPatch());
	}

	public static @Nullable ItemHandler getItemHandler(
		Level level,
		BlockPos pos,
		@Nullable BlockState state,
		@Nullable BlockEntity blockEntity,
		@Nullable Direction direction
	) {
		ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, state, blockEntity, direction);
		return handler == null ? null : new ResourceHandlerItemHandler(handler);
	}

	public static @Nullable FluidHandler getFluidHandler(
		Level level,
		BlockPos pos,
		@Nullable BlockState state,
		@Nullable BlockEntity blockEntity,
		@Nullable Direction direction
	) {
		ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, state, blockEntity, direction);
		return handler == null ? null : new ResourceHandlerFluidHandler(handler);
	}

	public static <T extends BlockEntity> void registerItemHandler(BlockEntityType<T> type, TransferApi.ItemHandlerProvider<T> provider) {
		ITEM_REGISTRATIONS.add(new ItemRegistration<>(type, provider));
	}

	public static <T extends BlockEntity> void registerFluidHandler(BlockEntityType<T> type, TransferApi.FluidHandlerProvider<T> provider) {
		FLUID_REGISTRATIONS.add(new FluidRegistration<>(type, provider));
	}

	public static void flush(RegisterCapabilitiesEvent event) {
		for (ItemRegistration<?> registration : ITEM_REGISTRATIONS) registration.register(event);
		for (FluidRegistration<?> registration : FLUID_REGISTRATIONS) registration.register(event);
	}

	private record ItemRegistration<T extends BlockEntity>(BlockEntityType<T> type, TransferApi.ItemHandlerProvider<T> provider) {
		void register(RegisterCapabilitiesEvent event) {
			event.registerBlockEntity(
				Capabilities.Item.BLOCK,
				this.type,
				(be, direction) -> {
					Container container = this.provider.get(be, direction);
					if (container == null) return null;
					if (container instanceof WorldlyContainer worldly) return new WorldlyContainerWrapper(worldly, direction);
					return VanillaContainerWrapper.of(container);
				}
			);
		}
	}

	private record FluidRegistration<T extends BlockEntity>(BlockEntityType<T> type, TransferApi.FluidHandlerProvider<T> provider) {
		void register(RegisterCapabilitiesEvent event) {
			event.registerBlockEntity(
				Capabilities.Fluid.BLOCK,
				this.type,
				(be, direction) -> {
					FluidHandler handler = this.provider.get(be, direction);
					return handler == null ? null : new FluidHandlerResourceHandler(handler);
				}
			);
		}
	}

	public static ItemHandlerCache createItemHandlerCache(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> cache = BlockCapabilityCache.create(
			Capabilities.Item.BLOCK, level, pos, direction
		);
		return () -> {
			ResourceHandler<ItemResource> handler = cache.getCapability();
			return handler == null ? null : new ResourceHandlerItemHandler(handler);
		};
	}

	public static FluidHandlerCache createFluidHandlerCache(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction> cache = BlockCapabilityCache.create(
			Capabilities.Fluid.BLOCK, level, pos, direction
		);
		return () -> {
			ResourceHandler<FluidResource> handler = cache.getCapability();
			return handler == null ? null : new ResourceHandlerFluidHandler(handler);
		};
	}

	public static @Nullable FluidContainerItem getFluidContainer(ItemStack stack) {
		if (stack.isEmpty()) return null;

		SimpleContainer container = new SimpleContainer(stack.copy());
		ResourceHandler<ItemResource> itemHandler = VanillaContainerWrapper.of(container);
		ItemAccess access = ItemAccess.forHandlerIndex(itemHandler, 0);
		ResourceHandler<FluidResource> fluidHandler = access.getCapability(Capabilities.Fluid.ITEM);
		if (fluidHandler == null) return null;

		return new ContainerItemFluidHandler(container, fluidHandler);
	}
}
