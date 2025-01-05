/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.debug.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.core.client.api.FrustumUtil;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WindDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private final Minecraft minecraft;
	private List<Vec3> accessedWindPositions = Collections.emptyList();
	private List<List<Pair<Vec3, Integer>>> windNodes = Collections.emptyList();

	public WindDebugRenderer(Minecraft client) {
		this.minecraft = client;
	}

	public void tick() {
		this.accessedWindPositions = ClientWindManager.getAccessedPositions();
		this.windNodes = ImmutableList.copyOf(
			this.createAllWindNodes()
		);
	}

	@Override
	public void clear() {
		this.accessedWindPositions = Collections.emptyList();
		this.windNodes = Collections.emptyList();
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		renderWindNodesFromList(matrices, vertexConsumers, cameraX, cameraY, cameraZ, this.windNodes);
	}

	private @NotNull List<List<Pair<Vec3, Integer>>> createAllWindNodes() {
		List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
		this.accessedWindPositions.forEach(
			vec3 -> {
				if (FrustumUtil.isVisible(vec3, 0.5D)) {
					windNodes.add(createWindNodes(this.minecraft.level, vec3, 1.5D, false));
				}
			}
		);

		return windNodes;
	}

	protected static @NotNull List<Pair<Vec3, Integer>> createWindNodes(Level level, Vec3 origin, double stretch, boolean disturbanceOnly) {
		List<Pair<Vec3, Integer>> windNodes = new ArrayList<>();
		Vec3 wind = disturbanceOnly ?
			ClientWindManager.getRawDisturbanceMovement(level, origin)
			: ClientWindManager.getWindMovement(level, origin);
		double windlength = wind.length();
		if (windlength != 0D) {
			int increments = 3;
			Vec3 lineStart = origin;
			double windLineScale = (1D / increments) * stretch;
			windNodes.add(
				Pair.of(
					lineStart,
					calculateNodeColor(Math.min(1D, wind.length()), disturbanceOnly)
				)
			);

			for (int i = 0; i < increments; ++i) {
				Vec3 lineEnd = lineStart.add(wind.scale(windLineScale));
				windNodes.add(
					Pair.of(
						lineEnd,
						calculateNodeColor(Math.min(1D, wind.length()), disturbanceOnly)
					)
				);
				lineStart = lineEnd;
				wind = disturbanceOnly ?
					ClientWindManager.getRawDisturbanceMovement(level, lineStart)
					: ClientWindManager.getWindMovement(level, lineStart);
			}
		}

		return windNodes;
	}

	protected static int calculateNodeColor(double strength, boolean disturbanceOnly) {
		return ARGB.color(
			255,
			(int) Mth.lerp(strength, 255, 0),
			(int) Mth.lerp(strength, 90, 255),
			disturbanceOnly ? 0 : 255
		);
	}

	protected static void renderWindNodesFromList(
		PoseStack matrices,
		MultiBufferSource vertexConsumers,
		double cameraX,
		double cameraY,
		double cameraZ,
		@NotNull List<List<Pair<Vec3, Integer>>> windNodes
	) {
		windNodes.forEach(nodes -> renderWindNodes(matrices, vertexConsumers, cameraX, cameraY, cameraZ, nodes));
	}

	protected static void renderWindNodes(
		PoseStack matrices,
		MultiBufferSource vertexConsumers,
		double cameraX,
		double cameraY,
		double cameraZ,
		@NotNull List<Pair<Vec3, Integer>> windNodes
	) {
		int size = windNodes.size();
		if (size > 1) {
			for (int i = 1; i < windNodes.size(); i++) {
				Pair<Vec3, Integer> startNode = windNodes.get(i - 1);
				Pair<Vec3, Integer> endNode = windNodes.get(i);
				drawLine(
					matrices,
					vertexConsumers,
					cameraX,
					cameraY,
					cameraZ,
					startNode.getFirst(),
					endNode.getFirst(),
					startNode.getSecond()
				);
			}
		}
	}

	private static void drawLine(
		@NotNull PoseStack matrices,
		@NotNull MultiBufferSource vertexConsumers,
		double cameraX,
		double cameraY,
		double cameraZ,
		@NotNull Vec3 start,
		@NotNull Vec3 target,
		int color
	) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.debugLineStrip(24D));
		vertexConsumer.addVertex(matrices.last(), (float)(start.x - cameraX), (float)(start.y - cameraY), (float)(start.z - cameraZ)).setColor(color);
		vertexConsumer.addVertex(matrices.last(), (float)(target.x - cameraX), (float)(target.y - cameraY), (float)(target.z - cameraZ)).setColor(color);
	}
}
