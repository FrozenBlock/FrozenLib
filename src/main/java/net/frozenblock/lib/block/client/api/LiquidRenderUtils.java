/*
 * Copyright (C) 2025-2026 FrozenBlock
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
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.CardinalLighting;
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
		FluidRenderer fluidRenderer,
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
		final boolean renderDown = shouldRenderFace(fluidState, state, Direction.DOWN, downFluidState, downState) && !isFaceOccludedByNeighbor(Direction.DOWN, FluidRenderer.MAX_FLUID_HEIGHT, downState, state);
		final boolean renderNorth = shouldRenderFace(fluidState, state, Direction.NORTH, northFluidState, northState);
		final boolean renderSouth = shouldRenderFace(fluidState, state, Direction.SOUTH, southFluidState, southState);
		final boolean renderEast = shouldRenderFace(fluidState, state, Direction.EAST, eastFluidState, eastState);
		final boolean renderWest = shouldRenderFace(fluidState, state, Direction.WEST, westFluidState, westState);

		if (!(renderUp || renderDown || renderEast || renderWest || renderNorth || renderSouth)) return;

		int color = ARGB.white(1F);
		CardinalLighting cardinalLighting = level.cardinalLighting();

		float southWestHeight;
		float southEastHeight;
		float northWestHeight;
		float northEastHeight;
		final Fluid fluid = fluidState.getType();
		final float fluidHeight = fluidRenderer.getHeight(level, fluid, pos, state, fluidState);
		if (fluidHeight >= 1F) {
			northEastHeight = 1F;
			northWestHeight = 1F;
			southEastHeight = 1F;
			southWestHeight = 1F;
		} else {
			final float northHeight = fluidRenderer.getHeight(level, fluid, pos.north(), northState, northFluidState);
			final float southHeight = fluidRenderer.getHeight(level, fluid, pos.south(), southState, southFluidState);
			final float eastHeight = fluidRenderer.getHeight(level, fluid, pos.east(), eastState, eastFluidState);
			final float westHeight = fluidRenderer.getHeight(level, fluid, pos.west(), westState, westFluidState);
			northEastHeight = fluidRenderer.calculateAverageHeight(level, fluid, fluidHeight, northHeight, eastHeight, pos.relative(Direction.NORTH).relative(Direction.EAST));
			northWestHeight = fluidRenderer.calculateAverageHeight(level, fluid, fluidHeight, northHeight, westHeight, pos.relative(Direction.NORTH).relative(Direction.WEST));
			southEastHeight = fluidRenderer.calculateAverageHeight(level, fluid, fluidHeight, southHeight, eastHeight, pos.relative(Direction.SOUTH).relative(Direction.EAST));
			southWestHeight = fluidRenderer.calculateAverageHeight(level, fluid, fluidHeight, southHeight, westHeight, pos.relative(Direction.SOUTH).relative(Direction.WEST));
		}

		final float x = pos.getX() & 15;
		final float y = pos.getY() & 15;
		final float z = pos.getZ() & 15;
		final float bottomOffs = renderDown ? 0.001F : 0F;

		final float u0 = sprite.getU0();
		final float u1 = sprite.getU1();
		final float v0 = sprite.getV0();
		final float v1 = sprite.getV1();

		if (renderUp && !isFaceOccludedByNeighbor(Direction.UP, Math.min(Math.min(northWestHeight, southWestHeight), Math.min(southEastHeight, northEastHeight)), upState, state)) {
			northWestHeight -= 0.001F;
			southWestHeight -= 0.001F;
			southEastHeight -= 0.001F;
			northEastHeight -= 0.001F;

			final int topLightCoords = fluidRenderer.getLightCoords(level, pos);
			int upColor = ARGB.scaleRGB(color, cardinalLighting.up());
			fluidRenderer.vertex(vertexConsumer, x + 0F, y + northWestHeight, z + 0F, upColor, u0, v0, topLightCoords);
			fluidRenderer.vertex(vertexConsumer, x + 0F, y + southWestHeight, z + 1F, upColor, u0, v1, topLightCoords);
			fluidRenderer.vertex(vertexConsumer, x + 1F, y + southEastHeight, z + 1F, upColor, u1, v1, topLightCoords);
			fluidRenderer.vertex(vertexConsumer, x + 1F, y + northEastHeight, z + 0F, upColor, u1, v0, topLightCoords);
			if (fluidState.shouldRenderBackwardUpFace(level, pos.above()) || !state.equals(downState)) {
				fluidRenderer.vertex(vertexConsumer, x + 0F, y + northWestHeight, z + 0F, upColor, u0, v0, topLightCoords);
				fluidRenderer.vertex(vertexConsumer, x + 1F, y + northEastHeight, z + 0F, upColor, u1, v0, topLightCoords);
				fluidRenderer.vertex(vertexConsumer, x + 1F, y + southEastHeight, z + 1F, upColor, u1, v1, topLightCoords);
				fluidRenderer.vertex(vertexConsumer, x + 0F, y + southWestHeight, z + 1F, upColor, u0, v1, topLightCoords);
			}
		}

		if (renderDown) {
			int belowLightCoords = fluidRenderer.getLightCoords(level, pos.below());
			int belowColor = ARGB.scaleRGB(color, cardinalLighting.down());
			fluidRenderer.vertex(vertexConsumer, x, y + bottomOffs, z + 1F, belowColor, u0, v1, belowLightCoords);
			fluidRenderer.vertex(vertexConsumer, x, y + bottomOffs, z, belowColor, u0, v0, belowLightCoords);
			fluidRenderer.vertex(vertexConsumer, x + 1F, y + bottomOffs, z, belowColor, u1, v0, belowLightCoords);
			fluidRenderer.vertex(vertexConsumer, x + 1F, y + bottomOffs, z + 1F, belowColor, u1, v1, belowLightCoords);
			if (downState.getBlock() != state.getBlock() && !downState.canOcclude()) {
				fluidRenderer.vertex(vertexConsumer, x, y + bottomOffs, z + 1F, belowColor, u0, v1, belowLightCoords);
				fluidRenderer.vertex(vertexConsumer, x + 1F, y + bottomOffs, z + 1F, belowColor, u0, v0, belowLightCoords);
				fluidRenderer.vertex(vertexConsumer, x + 1F, y + bottomOffs, z, belowColor, u1, v0, belowLightCoords);
				fluidRenderer.vertex(vertexConsumer, x, y + bottomOffs, z, belowColor, u1, v1, belowLightCoords);
			}
		}

		final int sideLightCoords = fluidRenderer.getLightCoords(level, pos);
		for (Direction faceDir : Direction.Plane.HORIZONTAL) {
			float firstY;
			float secondY;
			float firstX;
			float secondX;
			float firstZ;
			float lastZ;

			if (!(switch (faceDir) {
				case NORTH -> {
					firstY = northWestHeight;
					secondY = northEastHeight;
					firstX = x;
					secondX = x + 1F;
					lastZ = firstZ = z + 0.001F;
					yield renderNorth;
				}
				case SOUTH -> {
					firstY = southEastHeight;
					secondY = southWestHeight;
					firstX = x + 1F;
					secondX = x;
					firstZ = lastZ = z + 1F - 0.001F;
					yield renderSouth;
				}
				case WEST -> {
					firstY = southWestHeight;
					secondY = northWestHeight;
					secondX = firstX = x + 0.001F;
					firstZ = z + 1F;
					lastZ = z;
					yield renderWest;
				}
				default -> {
					firstY = northEastHeight;
					secondY = southEastHeight;
					secondX = firstX = x + 1F - 0.001F;
					firstZ = z;
					lastZ = z + 1F;
					yield renderEast;
				}
			}) || isFaceOccludedByNeighbor(faceDir, Math.max(firstY, secondY), level.getBlockState(pos.relative(faceDir)), level.getBlockState(pos.relative(faceDir))))
				continue;

			// TODO 26.1 check
			final float shadeSide = faceDir.getAxis() == Direction.Axis.Z ? cardinalLighting.north() : cardinalLighting.west();
			final int faceColor = ARGB.scaleRGB(color, cardinalLighting.up() * shadeSide);
			fluidRenderer.vertex(vertexConsumer, firstX, y + firstY, firstZ, faceColor, u0, v0, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, secondX, y + secondY, lastZ, faceColor, u1, v0, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, secondX, y + bottomOffs, lastZ, faceColor, u1, v1, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, firstX, y + bottomOffs, firstZ, faceColor, u0, v1, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, firstX, y + bottomOffs, firstZ, faceColor, u0, v1, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, secondX, y + bottomOffs, lastZ, faceColor, u1, v1, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, secondX, y + secondY, lastZ, faceColor, u1, v0, sideLightCoords);
			fluidRenderer.vertex(vertexConsumer, firstX, y + firstY, firstZ, faceColor, u0, v0, sideLightCoords);
		}
	}

	private static boolean isNeighborSameFluidAndBlock(FluidState firstState, FluidState secondState, BlockState firstBlock, BlockState secondBlock) {
		return secondState.getType().isSame(firstState.getType()) && firstBlock.getBlock() == secondBlock.getBlock();
	}

	private static boolean isFaceOccludedByState(Direction face, float height, BlockState state, BlockState neighborState) {
		if (neighborState.getBlock() != state.getBlock() || !state.canOcclude()) return false;
		final VoxelShape planeShape = Shapes.box(0D, 0D, 0D, 1D, height, 1D);
		final VoxelShape occlusionShape = state.getOcclusionShape();
		return Shapes.blockOccludes(planeShape, occlusionShape, face);
	}

	private static boolean isFaceOccludedByNeighbor(Direction side, float height, BlockState state, BlockState neighborState) {
		return isFaceOccludedByState(side, height, state, neighborState);
	}

	private static boolean isFaceOccludedBySelf(BlockState state, Direction face, BlockState neighborState) {
		return isFaceOccludedByState(face.getOpposite(), 1F, state, neighborState);
	}

	public static boolean shouldRenderFace(
		FluidState fluidState,
		BlockState state,
		Direction side,
		FluidState neighborFluid,
		BlockState neighborState
	) {
		return !isFaceOccludedBySelf(state, side, neighborState) && !isNeighborSameFluidAndBlock(fluidState, neighborFluid, state, neighborState);
	}
}
