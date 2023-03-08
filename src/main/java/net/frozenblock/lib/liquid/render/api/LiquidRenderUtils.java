/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
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
import net.minecraft.world.phys.Vec3;
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
		float r;
		float q;
		float p;
		float o;
		int i = 0xFFFFFF;
		float f = (float)(i >> 16 & 0xFF) / 255.0f;
		float g = (float)(i >> 8 & 0xFF) / 255.0f;
		float h = (float)(i & 0xFF) / 255.0f;
		BlockState blockState2 = level.getBlockState(pos.relative(Direction.DOWN));
		FluidState fluidState2 = blockState2.getFluidState();
		BlockState blockState3 = level.getBlockState(pos.relative(Direction.UP));
		FluidState fluidState3 = blockState3.getFluidState();
		BlockState blockState4 = level.getBlockState(pos.relative(Direction.NORTH));
		FluidState fluidState4 = blockState4.getFluidState();
		BlockState blockState5 = level.getBlockState(pos.relative(Direction.SOUTH));
		FluidState fluidState5 = blockState5.getFluidState();
		BlockState blockState6 = level.getBlockState(pos.relative(Direction.WEST));
		FluidState fluidState6 = blockState6.getFluidState();
		BlockState blockState7 = level.getBlockState(pos.relative(Direction.EAST));
		FluidState fluidState7 = blockState7.getFluidState();
		boolean bl2 = !isNeighborSameFluid(fluidState, fluidState3);
		boolean bl3 = shouldRenderFace(level, pos, fluidState, blockState, Direction.DOWN, fluidState2) && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, 0.8888889f, blockState2);
		boolean bl4 = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, fluidState4);
		boolean bl5 = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, fluidState5);
		boolean bl6 = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, fluidState6);
		boolean bl7 = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, fluidState7);
		if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
			return;
		}
		float j = level.getShade(Direction.DOWN, true);
		float k = level.getShade(Direction.UP, true);
		float l = level.getShade(Direction.NORTH, true);
		float m = level.getShade(Direction.WEST, true);
		Fluid fluid = fluidState.getType();
		float n = getHeight(level, fluid, pos, blockState, fluidState);
		if (n >= 1.0f) {
			o = 1.0f;
			p = 1.0f;
			q = 1.0f;
			r = 1.0f;
		} else {
			float s = getHeight(level, fluid, pos.north(), blockState4, fluidState4);
			float t = getHeight(level, fluid, pos.south(), blockState5, fluidState5);
			float u = getHeight(level, fluid, pos.east(), blockState7, fluidState7);
			float v = getHeight(level, fluid, pos.west(), blockState6, fluidState6);
			o = calculateAverageHeight(level, fluid, n, s, u, pos.relative(Direction.NORTH).relative(Direction.EAST));
			p = calculateAverageHeight(level, fluid, n, s, v, pos.relative(Direction.NORTH).relative(Direction.WEST));
			q = calculateAverageHeight(level, fluid, n, t, u, pos.relative(Direction.SOUTH).relative(Direction.EAST));
			r = calculateAverageHeight(level, fluid, n, t, v, pos.relative(Direction.SOUTH).relative(Direction.WEST));
		}
		double d = pos.getX() & 0xF;
		double e = pos.getY() & 0xF;
		double w = pos.getZ() & 0xF;
		y = bl3 ? 0.001f : 0.0f;
		if (bl2 && !isFaceOccludedByNeighbor(level, pos, Direction.UP, Math.min(Math.min(p, r), Math.min(q, o)), blockState3)) {
			float ak;
			float aj;
			float ai;
			float ah;
			float aa;
			p -= 0.001f;
			r -= 0.001f;
			q -= 0.001f;
			o -= 0.001f;
			Vec3 vec3 = fluidState.getFlow(level, pos);
			if (vec3.x == 0.0 && vec3.z == 0.0) {
				z = textureAtlasSprite.getU(0.0);
				aa = textureAtlasSprite.getV(0.0);
				ab = z;
				ac = textureAtlasSprite.getV(16.0);
				ad = textureAtlasSprite.getU(16.0);
				ae = ac;
				af = ad;
				ag = aa;
			} else {
				ah = (float)Mth.atan2(vec3.z, vec3.x) - 1.5707964f;
				ai = Mth.sin(ah) * 0.25f;
				aj = Mth.cos(ah) * 0.25f;
				z = textureAtlasSprite.getU((-aj - ai) * 16);
				aa = textureAtlasSprite.getV((-aj + ai) * 16);
				ab = textureAtlasSprite.getU((-aj + ai) * 16);
				ac = textureAtlasSprite.getV((aj + ai) * 16);
				ad = textureAtlasSprite.getU((aj + ai) * 16);
				ae = textureAtlasSprite.getV((aj - ai) * 16);
				af = textureAtlasSprite.getU((aj - ai) * 16);
				ag = textureAtlasSprite.getV((-aj - ai) * 16);
			}
			float al = (z + ab + ad + af) / 4.0f;
			ah = (aa + ac + ae + ag) / 4.0f;
			ai = (float)textureAtlasSprite.getWidth() / (textureAtlasSprite.getU1() - textureAtlasSprite.getU0());
			aj = (float)textureAtlasSprite.getHeight() / (textureAtlasSprite.getV1() - textureAtlasSprite.getV0());
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
			vertex(vertexConsumer, d + 0.0, e + (double)p, w + 0.0, an, ao, ap, z, aa, am);
			vertex(vertexConsumer, d + 0.0, e + (double)r, w + 1.0, an, ao, ap, ab, ac, am);
			vertex(vertexConsumer, d + 1.0, e + (double)q, w + 1.0, an, ao, ap, ad, ae, am);
			vertex(vertexConsumer, d + 1.0, e + (double)o, w + 0.0, an, ao, ap, af, ag, am);
			if (fluidState.shouldRenderBackwardUpFace(level, pos.above()) || !blockState.equals(blockState2)) {
				vertex(vertexConsumer, d + 0.0, e + (double)p, w + 0.0, an, ao, ap, z, aa, am);
				vertex(vertexConsumer, d + 1.0, e + (double)o, w + 0.0, an, ao, ap, af, ag, am);
				vertex(vertexConsumer, d + 1.0, e + (double)q, w + 1.0, an, ao, ap, ad, ae, am);
				vertex(vertexConsumer, d + 0.0, e + (double)r, w + 1.0, an, ao, ap, ab, ac, am);
			}
		}
		if (bl3) {
			z = textureAtlasSprite.getU0();
			ab = textureAtlasSprite.getU1();
			ad = textureAtlasSprite.getV0();
			af = textureAtlasSprite.getV1();
			int aq = getLightColor(level, pos.below());
			ac = j * f;
			ae = j * g;
			ag = j * h;
			vertex(vertexConsumer, d, e + (double)y, w + 1.0, ac, ae, ag, z, af, aq);
			vertex(vertexConsumer, d, e + (double)y, w, ac, ae, ag, z, ad, aq);
			vertex(vertexConsumer, d + 1.0, e + (double)y, w, ac, ae, ag, ab, ad, aq);
			vertex(vertexConsumer, d + 1.0, e + (double)y, w + 1.0, ac, ae, ag, ab, af, aq);
			if (!fluidState2.equals(fluidState)) {
				vertex(vertexConsumer, d, e + (double)y, w + 1.0, ac, ae, ag, z, af, aq);
				vertex(vertexConsumer, d + 1.0, e + (double)y, w, ac, ae, ag, z, ad, aq);
				vertex(vertexConsumer, d + 1.0, e + (double)y, w, ac, ae, ag, ab, ad, aq);
				vertex(vertexConsumer, d, e + (double)y, w, ac, ae, ag, ab, af, aq);
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
					af = p;
					aa = o;
					as = d;
					at = d + 1.0;
					au = w + (double)0.001f;
					av = w + (double)0.001f;
					yield bl4;
				}
				case SOUTH -> {
					af = q;
					aa = r;
					as = d + 1.0;
					at = d;
					au = w + 1.0 - (double)0.001f;
					av = w + 1.0 - (double)0.001f;
					yield bl5;
				}
				case WEST -> {
					af = r;
					aa = p;
					as = d + (double)0.001f;
					at = d + (double)0.001f;
					au = w + 1.0;
					av = w;
					yield bl6;
				}
				default -> {
					af = o;
					aa = q;
					as = d + 1.0 - (double)0.001f;
					at = d + 1.0 - (double)0.001f;
					au = w;
					av = w + 1.0;
					yield bl7;
				}
			}) || isFaceOccludedByNeighbor(level, pos, direction, Math.max(af, aa), level.getBlockState(pos.relative(direction)))) continue;
			ao = textureAtlasSprite.getU(0);
			ap = textureAtlasSprite.getU(16);
			float aw = textureAtlasSprite.getV(0);
			float ax = textureAtlasSprite.getV(0);
			float ay = textureAtlasSprite.getV(16);
			float az = direction.getAxis() == Direction.Axis.Z ? l : m;
			float ba = k * az * f;
			float bb = k * az * g;
			float bc = k * az * h;
			vertex(vertexConsumer, as, e + (double)af, au, ba, bb, bc, ao, aw, ar);
			vertex(vertexConsumer, at, e + (double)aa, av, ba, bb, bc, ap, ax, ar);
			vertex(vertexConsumer, at, e + (double)y, av, ba, bb, bc, ap, ay, ar);
			vertex(vertexConsumer, as, e + (double)y, au, ba, bb, bc, ao, ay, ar);
			vertex(vertexConsumer, as, e + (double)y, au, ba, bb, bc, ao, ay, ar);
			vertex(vertexConsumer, at, e + (double)y, av, ba, bb, bc, ap, ay, ar);
			vertex(vertexConsumer, at, e + (double)aa, av, ba, bb, bc, ap, ax, ar);
			vertex(vertexConsumer, as, e + (double)af, au, ba, bb, bc, ao, aw, ar);
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
		if (!blockState.getMaterial().isSolid()) {
			return 0.0f;
		}
		return -1.0f;
	}

	//xvertex
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
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private static boolean isNeighborSameFluid(FluidState firstState, FluidState secondState) {
		return secondState.getType().isSame(firstState.getType());
	}

	private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state) {
		if (state.canOcclude()) {
			VoxelShape voxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0);
			VoxelShape voxelShape2 = state.getOcclusionShape(level, pos);
			return Shapes.blockOccudes(voxelShape, voxelShape2, face);
		}
		return false;
	}

	private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState) {
		return isFaceOccludedByState(level, side, height, pos.relative(side), blockState);
	}

	private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face) {
		return isFaceOccludedByState(level, face.getOpposite(), 1.0f, pos, state);
	}

	public static boolean shouldRenderFace(BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState blockState, Direction side, FluidState neighborFluid) {
		return !isFaceOccludedBySelf(level, pos, blockState, side) && !isNeighborSameFluid(fluidState, neighborFluid);
	}
}
