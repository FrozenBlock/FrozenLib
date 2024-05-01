/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.liquid.render.api;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidRenderUtils {

	public static void tesselateWithSingleTexture(BlockAndTintGetter level, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, TextureAtlasSprite textureAtlasSprite) {
		float ap;
		float ao;
		float ag;
		float af;
		float ae;
		float ad;
		float ac;
		float ab;
		float z;
		float y;
		float southWestHeight;
		float southEastHeight;
		float northWestHeight;
		float northEastHeight;
		float f = (0xFFFFFF >> 16 & 0xFF) / 255.0f;
		float g = (0xFFFFFF >> 8 & 0xFF) / 255.0f;
		float h = (0xFFFFFF & 0xFF) / 255.0f;
		BlockState downBlockState = level.getBlockState(pos.relative(Direction.DOWN));
		FluidState downFluidState = downBlockState.getFluidState();
		BlockState upBlockState = level.getBlockState(pos.relative(Direction.UP));
		FluidState upFluidState = upBlockState.getFluidState();
		BlockState northBlockState = level.getBlockState(pos.relative(Direction.NORTH));
		FluidState northFluidState = northBlockState.getFluidState();
		BlockState southBlockState = level.getBlockState(pos.relative(Direction.SOUTH));
		FluidState southFluidState = southBlockState.getFluidState();
		BlockState westBlockState = level.getBlockState(pos.relative(Direction.WEST));
		FluidState westFluidState = westBlockState.getFluidState();
		BlockState eastBlockState = level.getBlockState(pos.relative(Direction.EAST));
		FluidState eastFluidState = eastBlockState.getFluidState();
		boolean shouldRenderUp = !isNeighborSameFluidAndBlock(fluidState, upFluidState, blockState, upBlockState);
		boolean shouldRenderDown = shouldRenderFace(level, pos, fluidState, blockState, Direction.DOWN, downFluidState, downBlockState) && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, 0.8888889f, downBlockState, blockState);
		boolean shouldRenderNorth = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, northFluidState, northBlockState);
		boolean bl5 = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, southFluidState, southBlockState);
		boolean bl6 = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, westFluidState, westBlockState);
		boolean bl7 = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, eastFluidState, eastBlockState);
		if (!(shouldRenderUp || shouldRenderDown || bl7 || bl6 || shouldRenderNorth || bl5)) {
			return;
		}
		float j = level.getShade(Direction.DOWN, true);
		float k = level.getShade(Direction.UP, true);
		float l = level.getShade(Direction.NORTH, true);
		float m = level.getShade(Direction.WEST, true);
		Fluid fluid = fluidState.getType();
		float n = getHeight(level, fluid, pos, blockState, fluidState);
		if (n >= 1.0f) {
			northEastHeight = 1.0f;
			northWestHeight = 1.0f;
			southEastHeight = 1.0f;
			southWestHeight = 1.0f;
		} else {
			float s = getHeight(level, fluid, pos.north(), northBlockState, northFluidState);
			float t = getHeight(level, fluid, pos.south(), southBlockState, southFluidState);
			float u = getHeight(level, fluid, pos.east(), eastBlockState, eastFluidState);
			float v = getHeight(level, fluid, pos.west(), westBlockState, westFluidState);
			northEastHeight = calculateAverageHeight(level, fluid, n, s, u, pos.relative(Direction.NORTH).relative(Direction.EAST));
			northWestHeight = calculateAverageHeight(level, fluid, n, s, v, pos.relative(Direction.NORTH).relative(Direction.WEST));
			southEastHeight = calculateAverageHeight(level, fluid, n, t, u, pos.relative(Direction.SOUTH).relative(Direction.EAST));
			southWestHeight = calculateAverageHeight(level, fluid, n, t, v, pos.relative(Direction.SOUTH).relative(Direction.WEST));
		}
		double d = pos.getX() & 0xF;
		double e = pos.getY() & 0xF;
		double w = pos.getZ() & 0xF;
		y = shouldRenderDown ? 0.001f : 0.0f;
		if (shouldRenderUp && !isFaceOccludedByNeighbor(level, pos, Direction.UP, Math.min(Math.min(northWestHeight, southWestHeight), Math.min(southEastHeight, northEastHeight)), upBlockState, blockState)) {
			float ak;
			float aj;
			float ai;
			float ah;
			float aa;
			northWestHeight -= 0.001f;
			southWestHeight -= 0.001f;
			southEastHeight -= 0.001f;
			northEastHeight -= 0.001f;
			z = textureAtlasSprite.getU(0.0F);
			aa = textureAtlasSprite.getV(0.0F);
			ab = z;
			ac = textureAtlasSprite.getV(1.0F);
			ad = textureAtlasSprite.getU(1.0F);
			ae = ac;
			af = ad;
			ag = aa;
			float al = (z + ab + ad + af) / 4.0f;
			ah = (aa + ac + ae + ag) / 4.0f;
			ai = textureAtlasSprite.contents().width() / (textureAtlasSprite.getU1() - textureAtlasSprite.getU0());
			aj = textureAtlasSprite.contents().height() / (textureAtlasSprite.getV1() - textureAtlasSprite.getV0());
			ak = 4.0f / Math.max(aj, ai);
			z = Mth.lerp(ak, z, al);
			ab = Mth.lerp(ak, ab, al);
			ad = Mth.lerp(ak, ad, al);
			af = Mth.lerp(ak, af, al);
			aa = Mth.lerp(ak, aa, ah);
			ac = Mth.lerp(ak, ac, ah);
			ae = Mth.lerp(ak, ae, ah);
			ag = Mth.lerp(ak, ag, ah);
			int am = getLightColor(level, pos);
			float an = k * f;
			ao = k * g;
			ap = k * h;
			vertex(vertexConsumer, d + 0.0, e + northWestHeight, w + 0.0, an, ao, ap, z, aa, am);
			vertex(vertexConsumer, d + 0.0, e + southWestHeight, w + 1.0, an, ao, ap, ab, ac, am);
			vertex(vertexConsumer, d + 1.0, e + southEastHeight, w + 1.0, an, ao, ap, ad, ae, am);
			vertex(vertexConsumer, d + 1.0, e + northEastHeight, w + 0.0, an, ao, ap, af, ag, am);
			if (fluidState.shouldRenderBackwardUpFace(level, pos.above()) || !blockState.equals(downBlockState)) {
				vertex(vertexConsumer, d + 0.0, e + northWestHeight, w + 0.0, an, ao, ap, z, aa, am);
				vertex(vertexConsumer, d + 1.0, e + northEastHeight, w + 0.0, an, ao, ap, af, ag, am);
				vertex(vertexConsumer, d + 1.0, e + southEastHeight, w + 1.0, an, ao, ap, ad, ae, am);
				vertex(vertexConsumer, d + 0.0, e + southWestHeight, w + 1.0, an, ao, ap, ab, ac, am);
			}
		}
		if (shouldRenderDown) {
			z = textureAtlasSprite.getU0();
			ab = textureAtlasSprite.getU1();
			ad = textureAtlasSprite.getV0();
			af = textureAtlasSprite.getV1();
			int aq = getLightColor(level, pos.below());
			ac = j * f;
			ae = j * g;
			ag = j * h;
			vertex(vertexConsumer, d, e + y, w + 1.0, ac, ae, ag, z, af, aq);
			vertex(vertexConsumer, d, e + y, w, ac, ae, ag, z, ad, aq);
			vertex(vertexConsumer, d + 1.0, e + y, w, ac, ae, ag, ab, ad, aq);
			vertex(vertexConsumer, d + 1.0, e + y, w + 1.0, ac, ae, ag, ab, af, aq);
			if (!downFluidState.equals(fluidState) && downBlockState.getBlock() != blockState.getBlock() && !downBlockState.canOcclude()) {
				vertex(vertexConsumer, d, e + y, w + 1.0, ac, ae, ag, z, af, aq);
				vertex(vertexConsumer, d + 1.0, e + y, w + 1.0, ac, ae, ag, z, ad, aq);
				vertex(vertexConsumer, d + 1.0, e + y, w, ac, ae, ag, ab, ad, aq);
				vertex(vertexConsumer, d, e + y, w, ac, ae, ag, ab, af, aq);
			}
		}
		int ar = getLightColor(level, pos);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			double av;
			double au;
			double at;
			double as;
			float aa;
			if (!(switch (direction) {
				case NORTH -> {
					af = northWestHeight;
					aa = northEastHeight;
					as = d;
					at = d + 1.0;
					au = w + 0.001f;
					av = w + 0.001f;
					yield shouldRenderNorth;
				}
				case SOUTH -> {
					af = southEastHeight;
					aa = southWestHeight;
					as = d + 1.0;
					at = d;
					au = w + 1.0 - 0.001f;
					av = w + 1.0 - 0.001f;
					yield bl5;
				}
				case WEST -> {
					af = southWestHeight;
					aa = northWestHeight;
					as = d + 0.001f;
					at = d + 0.001f;
					au = w + 1.0;
					av = w;
					yield bl6;
				}
				default -> {
					af = northEastHeight;
					aa = southEastHeight;
					as = d + 1.0 - 0.001f;
					at = d + 1.0 - 0.001f;
					au = w;
					av = w + 1.0;
					yield bl7;
				}
			}) || isFaceOccludedByNeighbor(level, pos, direction, Math.max(af, aa), level.getBlockState(pos.relative(direction)), level.getBlockState(pos.relative(direction)))) continue;
			ao = textureAtlasSprite.getU(0);
			ap = textureAtlasSprite.getU(1);
			float aw = textureAtlasSprite.getV(0);
			float ax = textureAtlasSprite.getV(0);
			float ay = textureAtlasSprite.getV(1);
			float az = direction.getAxis() == Direction.Axis.Z ? l : m;
			float ba = k * az * f;
			float bb = k * az * g;
			float bc = k * az * h;
			vertex(vertexConsumer, as, e + af, au, ba, bb, bc, ao, aw, ar);
			vertex(vertexConsumer, at, e + aa, av, ba, bb, bc, ap, ax, ar);
			vertex(vertexConsumer, at, e + y, av, ba, bb, bc, ap, ay, ar);
			vertex(vertexConsumer, as, e + y, au, ba, bb, bc, ao, ay, ar);
			vertex(vertexConsumer, as, e + y, au, ba, bb, bc, ao, ay, ar);
			vertex(vertexConsumer, at, e + y, av, ba, bb, bc, ap, ay, ar);
			vertex(vertexConsumer, at, e + aa, av, ba, bb, bc, ap, ax, ar);
			vertex(vertexConsumer, as, e + af, au, ba, bb, bc, ao, aw, ar);
		}
	}

	private static float calculateAverageHeight(BlockAndTintGetter world, Fluid fluid, float height, float adjacentHeightA, float adjacentHeightB, BlockPos fluidPos) {
		if (adjacentHeightB >= 1.0f || adjacentHeightA >= 1.0f) {
			return 1.0f;
		}
		float[] fs = new float[2];
		if (adjacentHeightB > 0.0f || adjacentHeightA > 0.0f) {
			float f = getHeight(world, fluid, fluidPos);
			if (f >= 1.0f) {
				return 1.0f;
			}
			addWeightedHeight(fs, f);
		}
		addWeightedHeight(fs, height);
		addWeightedHeight(fs, adjacentHeightB);
		addWeightedHeight(fs, adjacentHeightA);
		return fs[0] / fs[1];
	}

	public static void addWeightedHeight(float[] weights, float height) {
		if (height >= 0.8f) {
			weights[0] = weights[0] + height * 10.0f;
			weights[1] = weights[1] + 10.0f;
		} else if (height >= 0.0f) {
			weights[0] = weights[0] + height;
			weights[1] = weights[1] + 1.0f;
		}
	}

	public static float getHeight(BlockAndTintGetter world, Fluid fluid, BlockPos blockState) {
		BlockState blockState2 = world.getBlockState(blockState);
		return getHeight(world, fluid, blockState, blockState2, blockState2.getFluidState());
	}

	public static float getHeight(BlockAndTintGetter world, Fluid fluid, BlockPos pos, BlockState blockState, FluidState state) {
		if (fluid.isSame(state.getType())) {
			BlockState blockState2 = world.getBlockState(pos.above());
			if (fluid.isSame(blockState2.getFluidState().getType())) {
				return 1.0f;
			}
			return state.getOwnHeight();
		}
		if (!blockState.isSolid()) {
			return 0.0f;
		}
		return -1.0f;
	}

	//vertex
	public static void vertex(VertexConsumer consumer, double x, double y, double z, float red, float green, float blue, float u, float v, int packedLight) {
		consumer.vertex(x, y, z).color(red, green, blue, 1.0f).uv(u, v).uv2(packedLight).normal(0.0f, 1.0f, 0.0f).endVertex();
	}

	public static int getLightColor(BlockAndTintGetter level, BlockPos pos) {
		int i = LevelRenderer.getLightColor(level, pos);
		int j = LevelRenderer.getLightColor(level, pos.above());
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (Math.max(k, l)) | (Math.max(m, n)) << 16;
	}

	private static boolean isNeighborSameFluidAndBlock(FluidState firstState, FluidState secondState, BlockState firstBlock, BlockState secondBlock) {
		return secondState.getType().isSame(firstState.getType()) && firstBlock.getBlock() == secondBlock.getBlock();
	}

	private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state, BlockState neighborState) {
		if (neighborState.getBlock() == state.getBlock() && state.canOcclude()) {
			VoxelShape voxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0);
			VoxelShape voxelShape2 = state.getOcclusionShape(level, pos);
			return Shapes.blockOccudes(voxelShape, voxelShape2, face);
		}
		return false;
	}

	private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState, BlockState neighborState) {
		return isFaceOccludedByState(level, side, height, pos.relative(side), blockState, neighborState);
	}

	private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face, BlockState neighborState) {
		return isFaceOccludedByState(level, face.getOpposite(), 1.0f, pos, state, neighborState);
	}

	public static boolean shouldRenderFace(BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState blockState, Direction side, FluidState neighborFluid, BlockState neighborState) {
		return !isFaceOccludedBySelf(level, pos, blockState, side, neighborState) && !isNeighborSameFluidAndBlock(fluidState, neighborFluid, blockState, neighborState);
	}
}
