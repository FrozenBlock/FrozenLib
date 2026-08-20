package net.frozenblock.lib.transfer.api;

import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
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

@UtilityClass
public final class TransferApi {

	@PlatformImpl
	public static boolean hasItemStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static int insertItem(Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, boolean simulate) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static ItemStack extractItem(
		Level level,
		BlockPos pos,
		@Nullable Direction direction,
		@Nullable Predicate<ItemStack> filter,
		int maxAmount,
		boolean simulate
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static int moveItems(
		Level level,
		BlockPos fromPos,
		@Nullable Direction fromDirection,
		BlockPos toPos,
		@Nullable Direction toDirection,
		@Nullable Predicate<ItemStack> filter,
		int maxAmount
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean supportsItemInsertion(Level level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean supportsItemExtraction(Level level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean hasFluidStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static int insertFluid(Level level, BlockPos pos, @Nullable Direction direction, FluidVariant variant, int maxAmount, boolean simulate) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static FluidStack extractFluid(
		Level level,
		BlockPos pos,
		@Nullable Direction direction,
		@Nullable Predicate<FluidVariant> filter,
		int maxAmount,
		boolean simulate
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static int moveFluids(
		Level level,
		BlockPos fromPos,
		@Nullable Direction fromDirection,
		BlockPos toPos,
		@Nullable Direction toDirection,
		@Nullable Predicate<FluidVariant> filter,
		int maxAmount
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean supportsFluidInsertion(Level level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean supportsFluidExtraction(Level level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static @Nullable ItemHandler getItemHandler(
		Level level,
		BlockPos pos,
		@Nullable BlockState state,
		@Nullable BlockEntity blockEntity,
		@Nullable Direction direction
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static @Nullable FluidHandler getFluidHandler(
		Level level,
		BlockPos pos,
		@Nullable BlockState state,
		@Nullable BlockEntity blockEntity,
		@Nullable Direction direction
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T extends BlockEntity> void registerItemHandler(BlockEntityType<T> type, ItemHandlerProvider<T> provider) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T extends BlockEntity> void registerFluidHandler(BlockEntityType<T> type, FluidHandlerProvider<T> provider) {
		throw new AssertionError();
	}

	@FunctionalInterface
	public interface ItemHandlerProvider<T extends BlockEntity> {
		@Nullable Container get(T blockEntity, @Nullable Direction direction);
	}

	@FunctionalInterface
	public interface FluidHandlerProvider<T extends BlockEntity> {
		@Nullable FluidHandler get(T blockEntity, @Nullable Direction direction);
	}

	@PlatformImpl
	public static ItemHandlerCache createItemHandlerCache(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static FluidHandlerCache createFluidHandlerCache(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static @Nullable FluidContainerItem getFluidContainer(ItemStack stack) {
		throw new AssertionError();
	}

	public static boolean hasFluidContainer(ItemStack stack) {
		return getFluidContainer(stack) != null;
	}
}
