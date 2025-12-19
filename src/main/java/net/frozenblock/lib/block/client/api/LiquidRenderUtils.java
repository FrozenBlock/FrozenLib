/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.client.api;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Environment(EnvType.CLIENT)
public class LiquidRenderUtils {

	/**
	 * Renders a block as a fluid, with a single texture.
	 *
	 * @param level The current level.
	 * @param pos The position of the block being rendered.
	 * @param vertexConsumer The {@link VertexConsumer} to render with.
	 * @param state The {@link BlockState} being rendered.
	 * @param fluidState The {@link FluidState} to render as.
	 * @param sprite The texture to render.
	 */
	public static void tesselateWithSingleTexture(
		BlockAndTintGetter level,
		BlockPos pos,
		VertexConsumer vertexConsumer,
		BlockState state,
		FluidState fluidState,
		TextureAtlasSprite sprite
	) {
		final BlockState downState = level.getBlockState(pos.relative(Direction.DOWN));
		final FluidState downFluidState = downState.getFluidState();
		final BlockState upState = level.getBlockState(pos.relative(Direction.UP));
		final FluidState upFluidState = upState.getFluidState();
		final BlockState northState = level.getBlockState(pos.relative(Direction.NORTH));
		final FluidState northFluidState = northState.getFluidState();
		final BlockState southState = level.getBlockState(pos.relative(Direction.SOUTH));
		final FluidState southFluidState = southState.getFluidState();
		final BlockState westState = level.getBlockState(pos.relative(Direction.WEST));
		final FluidState westFluidState = westState.getFluidState();
		final BlockState eastState = level.getBlockState(pos.relative(Direction.EAST));
		final FluidState eastFluidState = eastState.getFluidState();

		final boolean renderUp = !isNeighborSameFluidAndBlock(fluidState, upFluidState, state, upState);
		final boolean renderDown = shouldRenderFace(level, pos, fluidState, state, Direction.DOWN, downFluidState, downState) && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, 0.8888889F, downState, state);
		final boolean renderNorth = shouldRenderFace(level, pos, fluidState, state, Direction.NORTH, northFluidState, northState);
		final boolean renderSouth = shouldRenderFace(level, pos, fluidState, state, Direction.SOUTH, southFluidState, southState);
		final boolean renderEast = shouldRenderFace(level, pos, fluidState, state, Direction.EAST, eastFluidState, eastState);
		final boolean renderWest = shouldRenderFace(level, pos, fluidState, state, Direction.WEST, westFluidState, westState);

		if (!(renderUp || renderDown || renderEast || renderWest || renderNorth || renderSouth)) return;

		float downShade = level.getShade(Direction.DOWN, true);
		float upShade = level.getShade(Direction.UP, true);
		float northShade = level.getShade(Direction.NORTH, true);
		float westShade = level.getShade(Direction.WEST, true);

		float southWestHeight;
		float southEastHeight;
		float northWestHeight;
		float northEastHeight;
		final Fluid fluid = fluidState.getType();
		final float fluidHeight = getHeight(level, fluid, pos, state, fluidState);
		if (fluidHeight >= 1F) {
			northEastHeight = 1F;
			northWestHeight = 1F;
			southEastHeight = 1F;
			southWestHeight = 1F;
		} else {
			final float northHeight = getHeight(level, fluid, pos.north(), northState, northFluidState);
			final float southHeight = getHeight(level, fluid, pos.south(), southState, southFluidState);
			final float eastHeight = getHeight(level, fluid, pos.east(), eastState, eastFluidState);
			final float westHeight = getHeight(level, fluid, pos.west(), westState, westFluidState);
			northEastHeight = calculateAverageHeight(level, fluid, fluidHeight, northHeight, eastHeight, pos.relative(Direction.NORTH).relative(Direction.EAST));
			northWestHeight = calculateAverageHeight(level, fluid, fluidHeight, northHeight, westHeight, pos.relative(Direction.NORTH).relative(Direction.WEST));
			southEastHeight = calculateAverageHeight(level, fluid, fluidHeight, southHeight, eastHeight, pos.relative(Direction.SOUTH).relative(Direction.EAST));
			southWestHeight = calculateAverageHeight(level, fluid, fluidHeight, southHeight, westHeight, pos.relative(Direction.SOUTH).relative(Direction.WEST));
		}

		final float renderX = pos.getX() & 0xF;
		final float renderY = pos.getY() & 0xF;
		final float renderZ = pos.getZ() & 0xF;
		final float renderYOffset = renderDown ? 0.001F : 0F;

		final float u0 = sprite.getU0();
		final float u1 = sprite.getU1();
		final float v0 = sprite.getV0();
		final float v1 = sprite.getV1();

		if (renderUp && !isFaceOccludedByNeighbor(level, pos, Direction.UP, Math.min(Math.min(northWestHeight, southWestHeight), Math.min(southEastHeight, northEastHeight)), upState, state)) {
			northWestHeight -= 0.001F;
			southWestHeight -= 0.001F;
			southEastHeight -= 0.001F;
			northEastHeight -= 0.001F;

			final int color = getLightCoords(level, pos);
			vertex(vertexConsumer, renderX + 0F, renderY + northWestHeight, renderZ + 0F, upShade, upShade, upShade, u0, v0, color);
			vertex(vertexConsumer, renderX + 0F, renderY + southWestHeight, renderZ + 1F, upShade, upShade, upShade, u0, v1, color);
			vertex(vertexConsumer, renderX + 1F, renderY + southEastHeight, renderZ + 1F, upShade, upShade, upShade, u1, v1, color);
			vertex(vertexConsumer, renderX + 1F, renderY + northEastHeight, renderZ + 0F, upShade, upShade, upShade, u1, v0, color);
			if (fluidState.shouldRenderBackwardUpFace(level, pos.above()) || !state.equals(downState)) {
				vertex(vertexConsumer, renderX + 0F, renderY + northWestHeight, renderZ + 0F, upShade, upShade, upShade, u0, v0, color);
				vertex(vertexConsumer, renderX + 1F, renderY + northEastHeight, renderZ + 0F, upShade, upShade, upShade, u1, v0, color);
				vertex(vertexConsumer, renderX + 1F, renderY + southEastHeight, renderZ + 1F, upShade, upShade, upShade, u1, v1, color);
				vertex(vertexConsumer, renderX + 0F, renderY + southWestHeight, renderZ + 1F, upShade, upShade, upShade, u0, v1, color);
			}
		}

		if (renderDown) {
			final int belowColor = getLightCoords(level, pos.below());
			vertex(vertexConsumer, renderX, renderY + renderYOffset, renderZ + 1F, downShade, downShade, downShade, u0, v1, belowColor);
			vertex(vertexConsumer, renderX, renderY + renderYOffset, renderZ, downShade, downShade, downShade, u0, v0, belowColor);
			vertex(vertexConsumer, renderX + 1F, renderY + renderYOffset, renderZ, downShade, downShade, downShade, u1, v0, belowColor);
			vertex(vertexConsumer, renderX + 1F, renderY + renderYOffset, renderZ + 1F, downShade, downShade, downShade, u1, v1, belowColor);
			if (downState.getBlock() != state.getBlock() && !downState.canOcclude()) {
				vertex(vertexConsumer, renderX, renderY + renderYOffset, renderZ + 1F, downShade, downShade, downShade, u0, v1, belowColor);
				vertex(vertexConsumer, renderX + 1F, renderY + renderYOffset, renderZ + 1F, downShade, downShade, downShade, u0, v0, belowColor);
				vertex(vertexConsumer, renderX + 1F, renderY + renderYOffset, renderZ, downShade, downShade, downShade, u1, v0, belowColor);
				vertex(vertexConsumer, renderX, renderY + renderYOffset, renderZ, downShade, downShade, downShade, u1, v1, belowColor);
			}
		}

		final int color = getLightCoords(level, pos);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			float firstY;
			float secondY;
			float firstX;
			float secondX;
			float firstZ;
			float lastZ;

			if (!(switch (direction) {
				case NORTH -> {
					firstY = northWestHeight;
					secondY = northEastHeight;
					firstX = renderX;
					secondX = renderX + 1F;
					lastZ = firstZ = renderZ + 0.001F;
					yield renderNorth;
				}
				case SOUTH -> {
					firstY = southEastHeight;
					secondY = southWestHeight;
					firstX = renderX + 1F;
					secondX = renderX;
					firstZ = lastZ = renderZ + 1F - 0.001F;
					yield renderSouth;
				}
				case WEST -> {
					firstY = southWestHeight;
					secondY = northWestHeight;
					secondX = firstX = renderX + 0.001F;
					firstZ = renderZ + 1F;
					lastZ = renderZ;
					yield renderWest;
				}
				default -> {
					firstY = northEastHeight;
					secondY = southEastHeight;
					secondX = firstX = renderX + 1F - 0.001F;
					firstZ = renderZ;
					lastZ = renderZ + 1F;
					yield renderEast;
				}
			}) || isFaceOccludedByNeighbor(level, pos, direction, Math.max(firstY, secondY), level.getBlockState(pos.relative(direction)), level.getBlockState(pos.relative(direction))))
				continue;

			final float sideShade = upShade * (direction.getAxis() == Direction.Axis.Z ? northShade : westShade);
			vertex(vertexConsumer, firstX, renderY + firstY, firstZ, sideShade, sideShade, sideShade, u0, v0, color);
			vertex(vertexConsumer, secondX, renderY + secondY, lastZ, sideShade, sideShade, sideShade, u1, v0, color);
			vertex(vertexConsumer, secondX, renderY + renderYOffset, lastZ, sideShade, sideShade, sideShade, u1, v1, color);
			vertex(vertexConsumer, firstX, renderY + renderYOffset, firstZ, sideShade, sideShade, sideShade, u0, v1, color);
			vertex(vertexConsumer, firstX, renderY + renderYOffset, firstZ, sideShade, sideShade, sideShade, u0, v1, color);
			vertex(vertexConsumer, secondX, renderY + renderYOffset, lastZ, sideShade, sideShade, sideShade, u1, v1, color);
			vertex(vertexConsumer, secondX, renderY + secondY, lastZ, sideShade, sideShade, sideShade, u1, v0, color);
			vertex(vertexConsumer, firstX, renderY + firstY, firstZ, sideShade, sideShade, sideShade, u0, v0, color);
		}
	}

	private static float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float height, float adjacentHeightA, float adjacentHeightB, BlockPos fluidPos) {
		if (adjacentHeightB >= 1F || adjacentHeightA >= 1F) return 1F;

		float[] heightAndCount = new float[2];
		if (adjacentHeightB > 0F || adjacentHeightA > 0F) {
			final float fluidHeight = getHeight(level, fluid, fluidPos);
			if (fluidHeight >= 1F) return 1F;
			addWeightedHeight(heightAndCount, fluidHeight);
		}

		addWeightedHeight(heightAndCount, height);
		addWeightedHeight(heightAndCount, adjacentHeightB);
		addWeightedHeight(heightAndCount, adjacentHeightA);
		return heightAndCount[0] / heightAndCount[1];
	}

	public static void addWeightedHeight(float[] weights, float height) {
		if (height >= 0.8F) {
			weights[0] = weights[0] + height * 10F;
			weights[1] = weights[1] + 10F;
		} else if (height >= 0F) {
			weights[0] = weights[0] + height;
			weights[1] = weights[1] + 1F;
		}
	}

	public static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		return getHeight(level, fluid, pos, state, state.getFluidState());
	}

	public static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos, BlockState state, FluidState fluidState) {
		if (fluid.isSame(fluidState.getType())) {
			final BlockState aboveState = level.getBlockState(pos.above());
			if (fluid.isSame(aboveState.getFluidState().getType())) return 1F;
			return fluidState.getOwnHeight();
		}

		if (!state.isSolid()) return 0F;
		return -1F;
	}

	public static void vertex(VertexConsumer consumer, float x, float y, float z, float red, float green, float blue, float u, float v, int packedLight) {
		consumer.addVertex(x, y, z).setColor(red, green, blue, 1F).setUv(u, v).setLight(packedLight).setNormal(0F, 1F, 0F);
	}

	public static int getLightCoords(BlockAndTintGetter level, BlockPos pos) {
		final int color = LevelRenderer.getLightCoords(level, pos);
		final int aboveColor = LevelRenderer.getLightCoords(level, pos.above());
		final int k = color & 0xFF;
		final int l = aboveColor & 0xFF;
		final int m = color >> 16 & 0xFF;
		final int n = aboveColor >> 16 & 0xFF;
		return (Math.max(k, l)) | (Math.max(m, n)) << 16;
	}

	private static boolean isNeighborSameFluidAndBlock(FluidState firstState, FluidState secondState, BlockState firstBlock, BlockState secondBlock) {
		return secondState.getType().isSame(firstState.getType()) && firstBlock.getBlock() == secondBlock.getBlock();
	}

	private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state, BlockState neighborState) {
		if (neighborState.getBlock() != state.getBlock() || !state.canOcclude()) return false;
		final VoxelShape planeShape = Shapes.box(0D, 0D, 0D, 1D, height, 1D);
		final VoxelShape occlusionShape = state.getOcclusionShape();
		return Shapes.blockOccludes(planeShape, occlusionShape, face);
	}

	private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState state, BlockState neighborState) {
		return isFaceOccludedByState(level, side, height, pos.relative(side), state, neighborState);
	}

	private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face, BlockState neighborState) {
		return isFaceOccludedByState(level, face.getOpposite(), 1F, pos, state, neighborState);
	}

	public static boolean shouldRenderFace(
		BlockAndTintGetter level,
		BlockPos pos,
		FluidState fluidState,
		BlockState state,
		Direction side,
		FluidState neighborFluid,
		BlockState neighborState
	) {
		return !isFaceOccludedBySelf(level, pos, state, side, neighborState) && !isNeighborSameFluidAndBlock(fluidState, neighborFluid, state, neighborState);
	}
}
