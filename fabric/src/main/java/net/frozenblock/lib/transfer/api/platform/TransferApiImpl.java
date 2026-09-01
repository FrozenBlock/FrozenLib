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

import java.util.function.Predicate;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.frozenblock.lib.transfer.api.FluidStack;
import net.frozenblock.lib.transfer.api.FluidTransferVariant;
import net.frozenblock.lib.transfer.api.FluidContainerItem;
import net.frozenblock.lib.transfer.api.FluidHandler;
import net.frozenblock.lib.transfer.api.FluidHandlerCache;
import net.frozenblock.lib.transfer.api.ItemHandler;
import net.frozenblock.lib.transfer.api.ItemHandlerCache;
import net.frozenblock.lib.transfer.api.TransferApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class TransferApiImpl {
	private static final long DROPLETS_PER_MILLIBUCKET = FluidConstants.BUCKET / 1000L;

	public static boolean hasItemStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		return ItemStorage.SIDED.find(level, pos, direction) != null;
	}

	public static int insertItem(Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, boolean simulate) {
		final Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);
		if (storage == null || stack.isEmpty()) return 0;

		final ItemVariant variant = ItemVariant.of(stack);

		try (Transaction transaction = Transaction.openOuter()) {
			final int inserted = (int) storage.insert(variant, stack.getCount(), transaction);
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
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);
		if (storage == null || maxAmount <= 0) return ItemStack.EMPTY;

		Predicate<ItemVariant> variantFilter = filter == null ? variant -> true : variant -> filter.test(variant.toStack());

		try (Transaction transaction = Transaction.openOuter()) {
			final ItemVariant resource = StorageUtil.findExtractableResource(storage, variantFilter, transaction);
			if (resource == null) return ItemStack.EMPTY;

			final int extracted = (int) storage.extract(resource, maxAmount, transaction);
			if (extracted <= 0) return ItemStack.EMPTY;

			if (!simulate) transaction.commit();
			return resource.toStack(extracted);
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
		final Storage<ItemVariant> from = ItemStorage.SIDED.find(level, fromPos, fromDirection);
		final Storage<ItemVariant> to = ItemStorage.SIDED.find(level, toPos, toDirection);
		if (from == null || to == null || maxAmount <= 0) return 0;

		final Predicate<ItemVariant> variantFilter = filter == null
			? variant -> true
			: variant -> filter.test(variant.toStack());
		return (int) StorageUtil.move(from, to, variantFilter, maxAmount, null);
	}

	public static boolean supportsItemInsertion(Level level, BlockPos pos, @Nullable Direction direction) {
		final Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);
		return storage != null && storage.supportsInsertion();
	}

	public static boolean supportsItemExtraction(Level level, BlockPos pos, @Nullable Direction direction) {
		final Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);
		return storage != null && storage.supportsExtraction();
	}

	public static boolean hasFluidStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		return FluidStorage.SIDED.find(level, pos, direction) != null;
	}

	public static int insertFluid(Level level, BlockPos pos, @Nullable Direction direction, FluidTransferVariant variant, int maxAmount, boolean simulate) {
		final Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, direction);
		if (storage == null || variant.isBlank() || maxAmount <= 0) return 0;

		final FluidVariant fabricVariant = toFabric(variant);

		try (Transaction transaction = Transaction.openOuter()) {
			long inserted = storage.insert(fabricVariant, millibucketsToDroplets(maxAmount), transaction);
			if (!simulate) transaction.commit();
			return dropletsToMillibuckets(inserted);
		}
	}

	public static FluidStack extractFluid(
		Level level,
		BlockPos pos,
		@Nullable Direction direction,
		@Nullable Predicate<FluidTransferVariant> filter,
		int maxAmount,
		boolean simulate
	) {
		final Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, direction);
		if (storage == null || maxAmount <= 0) return FluidStack.EMPTY;

		final Predicate<FluidVariant> variantFilter = filter == null
			? variant -> true
			: variant -> filter.test(fromFabric(variant));

		try (Transaction transaction = Transaction.openOuter()) {
			final FluidVariant resource = StorageUtil.findExtractableResource(storage, variantFilter, transaction);
			if (resource == null) return FluidStack.EMPTY;

			final long extracted = storage.extract(resource, millibucketsToDroplets(maxAmount), transaction);
			if (extracted <= 0) return FluidStack.EMPTY;

			if (!simulate) transaction.commit();
			return new FluidStack(fromFabric(resource), dropletsToMillibuckets(extracted));
		}
	}

	public static int moveFluids(
		Level level,
		BlockPos fromPos,
		@Nullable Direction fromDirection,
		BlockPos toPos,
		@Nullable Direction toDirection,
		@Nullable Predicate<FluidTransferVariant> filter,
		int maxAmount
	) {
		final Storage<FluidVariant> from = FluidStorage.SIDED.find(level, fromPos, fromDirection);
		final Storage<FluidVariant> to = FluidStorage.SIDED.find(level, toPos, toDirection);
		if (from == null || to == null || maxAmount <= 0) return 0;

		final Predicate<FluidVariant> variantFilter = filter == null ? variant -> true : variant -> filter.test(fromFabric(variant));
		final long moved = StorageUtil.move(from, to, variantFilter, millibucketsToDroplets(maxAmount), null);
		return dropletsToMillibuckets(moved);
	}

	public static boolean supportsFluidInsertion(Level level, BlockPos pos, @Nullable Direction direction) {
		final Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, direction);
		return storage != null && storage.supportsInsertion();
	}

	public static boolean supportsFluidExtraction(Level level, BlockPos pos, @Nullable Direction direction) {
		final Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, direction);
		return storage != null && storage.supportsExtraction();
	}

	static FluidVariant toFabric(FluidTransferVariant variant) {
		return FluidVariant.of(variant.fluid(), variant.components());
	}

	static FluidTransferVariant fromFabric(FluidVariant variant) {
		return FluidTransferVariant.of(variant.getFluid(), variant.getComponentsPatch());
	}

	static long millibucketsToDroplets(int millibuckets) {
		return millibuckets * DROPLETS_PER_MILLIBUCKET;
	}

	static int dropletsToMillibuckets(long droplets) {
		return (int) (droplets / DROPLETS_PER_MILLIBUCKET);
	}

	public static @Nullable ItemHandler getItemHandler(
		Level level,
		BlockPos pos,
		@Nullable BlockState state,
		@Nullable BlockEntity blockEntity,
		@Nullable Direction direction
	) {
		final Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, state, blockEntity, direction);
		return storage == null ? null : new StorageItemHandler(storage);
	}

	public static @Nullable FluidHandler getFluidHandler(
		Level level,
		BlockPos pos,
		@Nullable BlockState state,
		@Nullable BlockEntity blockEntity,
		@Nullable Direction direction
	) {
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, state, blockEntity, direction);
		return storage == null ? null : new StorageFluidHandler(storage);
	}

	public static <T extends BlockEntity> void registerItemHandler(BlockEntityType<T> type, TransferApi.ItemHandlerProvider<T> provider) {
		ItemStorage.SIDED.registerForBlockEntity(
			(be, direction) -> {
				final Container container = provider.get(be, direction);
				return container == null ? null : ContainerStorage.of(container, direction);
			},
			type
		);
	}

	public static <T extends BlockEntity> void registerFluidHandler(BlockEntityType<T> type, TransferApi.FluidHandlerProvider<T> provider) {
		FluidStorage.SIDED.registerForBlockEntity(
			(be, direction) -> {
				final FluidHandler handler = provider.get(be, direction);
				return handler == null ? null : new FluidHandlerStorage(handler);
			},
			type
		);
	}

	public static ItemHandlerCache createItemHandlerCache(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		final BlockApiCache<Storage<ItemVariant>, @Nullable Direction> cache = BlockApiCache.create(ItemStorage.SIDED, level, pos);
		return () -> {
			Storage<ItemVariant> storage = cache.find(direction);
			return storage == null ? null : new StorageItemHandler(storage);
		};
	}

	public static FluidHandlerCache createFluidHandlerCache(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		final BlockApiCache<Storage<FluidVariant>, @Nullable Direction> cache = BlockApiCache.create(FluidStorage.SIDED, level, pos);
		return () -> {
			Storage<FluidVariant> storage = cache.find(direction);
			return storage == null ? null : new StorageFluidHandler(storage);
		};
	}

	public static @Nullable FluidContainerItem getFluidContainer(ItemStack stack) {
		if (stack.isEmpty()) return null;

		final MutableItemStackStorage stackStorage = new MutableItemStackStorage(stack.copy());
		final ContainerItemContext context = ContainerItemContext.ofSingleSlot(stackStorage);
		final Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, context);
		if (fluidStorage == null) return null;

		return new ContainerItemFluidHandler(stackStorage, fluidStorage);
	}

	private TransferApiImpl() {}
}
