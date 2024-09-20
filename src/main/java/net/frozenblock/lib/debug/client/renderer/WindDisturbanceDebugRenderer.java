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

package net.frozenblock.lib.debug.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.core.client.api.FrustumUtil;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WindDisturbanceDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private final Minecraft minecraft;
	private List<WindDisturbance<?>> windDisturbances = Collections.emptyList();
	private List<List<Pair<Vec3, Integer>>> windNodes = Collections.emptyList();

	public WindDisturbanceDebugRenderer(Minecraft client) {
		this.minecraft = client;
	}

	public void tick() {
		this.windDisturbances = ImmutableList.copyOf(
			ClientWindManager.getWindDisturbances()
		);
		this.windNodes = ImmutableList.copyOf(
			this.createAllWindNodes()
		);
	}

	@Override
	public void clear() {
		this.windDisturbances = Collections.emptyList();
		this.windNodes = Collections.emptyList();
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		this.windDisturbances.forEach(
			windDisturbance -> {
				LevelRenderer.renderVoxelShape(
					matrices,
					vertexConsumers.getBuffer(RenderType.lines()),
					Shapes.create(windDisturbance.affectedArea),
					-cameraX,
					-cameraY,
					-cameraZ,
					0.5F,
					1F,
					0.5F,
					0.35F,
					true
				);
				renderFilledBox(
					matrices,
					vertexConsumers,
					AABB.ofSize(windDisturbance.origin, 0.2D, 0.2D, 0.2D),
					cameraX, cameraY, cameraZ
				);
			}
		);

		WindDebugRenderer.renderWindNodesFromList(matrices, vertexConsumers, cameraX, cameraY, cameraZ, this.windNodes);
	}

	private @NotNull List<List<Pair<Vec3, Integer>>> createAllWindNodes() {
		List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
		this.windDisturbances.forEach(
			windDisturbance -> {
				if (FrustumUtil.isVisible(windDisturbance.affectedArea)) {
					BlockPos.betweenClosed(
						BlockPos.containing(windDisturbance.affectedArea.minX, windDisturbance.affectedArea.minY, windDisturbance.affectedArea.minZ),
						BlockPos.containing(windDisturbance.affectedArea.maxX, windDisturbance.affectedArea.maxY, windDisturbance.affectedArea.maxZ)
					).forEach(
						blockPos -> {
							Vec3 blockPosCenter = Vec3.atCenterOf(blockPos);
							windNodes.add(WindDebugRenderer.createWindNodes(this.minecraft.level, blockPosCenter, 1D, true));
						}
					);
				}
			}
		);
		return windNodes;
	}

	private static void renderFilledBox(
		PoseStack matrices,
		MultiBufferSource vertexConsumers,
		@NotNull AABB box,
		double cameraX, double cameraY, double cameraZ
	) {
		Vec3 vec3 = new Vec3(-cameraX, -cameraY, -cameraZ);
		DebugRenderer.renderFilledBox(matrices, vertexConsumers, box.move(vec3), 1F, 1F, 1F, 1F);
	}
}
