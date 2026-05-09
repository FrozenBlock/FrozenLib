package net.frozenblock.lib.block.impl.waterlike;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

public enum BubbleColumnDirection implements StringRepresentable {
	NONE("none", Optional.empty()),
	UP("up", Optional.of(Direction.UP)),
	DOWN("down", Optional.of(Direction.DOWN));
	private final Optional<Direction> direction;
	private final String name;

	BubbleColumnDirection(String name, Optional<Direction> direction) {
		this.name = name;
		this.direction = direction;
	}

	public Optional<Direction> direction() {
		return direction;
	}

	public static BubbleColumnDirection getFromBubbleColumn(BlockState bubbleColumnState) {
		return bubbleColumnState.getValue(BubbleColumnBlock.DRAG_DOWN) ? DOWN : UP;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
