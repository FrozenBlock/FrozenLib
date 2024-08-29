package net.frozenblock.lib.debug.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.debug.client.impl.DebugRenderManager;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WindDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private static final int ENTITY_LINE_COLOR = FastColor.ARGB32.color(255, 100, 255, 255);
	private final Minecraft minecraft;
	private List<WindDisturbance<?>> windDisturbances = Collections.emptyList();
	private List<Entity> entitiesToRender = Collections.emptyList();

	public WindDebugRenderer(Minecraft client) {
		this.minecraft = client;
	}

	public void tick() {
		this.windDisturbances = ImmutableList.copyOf(
			ClientWindManager.getWindDisturbances()
		);
		this.entitiesToRender = ImmutableList.copyOf(
			this.minecraft.level.entitiesForRendering()
		);
	}

	@Override
	public void clear() {
		this.windDisturbances = Collections.emptyList();
		this.entitiesToRender = Collections.emptyList();
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
				BlockPos.betweenClosed(
					BlockPos.containing(windDisturbance.affectedArea.getMinPosition()),
					BlockPos.containing(windDisturbance.affectedArea.getMaxPosition())
				).forEach(
					blockPos -> {
						Vec3 blockPosCenter = Vec3.atCenterOf(blockPos);
						Vec3 wind = ClientWindManager.getRawDisturbanceMovement(this.minecraft.level, blockPosCenter);
						double windlength = wind.length();
						if (windlength != 0D) {
							windlength = Math.min(1D, windlength);
							drawLine(
								matrices,
								vertexConsumers,
								cameraX, cameraY, cameraZ,
								blockPosCenter,
								blockPosCenter.add(wind),
								FastColor.ARGB32.color(
									255,
									(int) Mth.lerp(windlength, 255, 0),
									(int) Mth.lerp(windlength, 90, 255),
									0
								)
							);
							renderTransparentBox(
								matrices,
								vertexConsumers,
								AABB.ofSize(blockPosCenter, 0.5D, 0.5D, 0.5D),
								cameraX, cameraY, cameraZ,
								windlength
							);
						}
					}
				);
			}
		);

		this.entitiesToRender.forEach(
			entity -> {
				Vec3 entityStartPos = entity.getEyePosition(DebugRenderManager.PARTIAL_TICK);
				Vec3 entityWind = ClientWindManager.getWindMovement(entity.level(), entityStartPos);
				drawLine(
					matrices,
					vertexConsumers,
					cameraX, cameraY, cameraZ,
					entityStartPos,
					entityStartPos.add(entityWind.scale(3D)),
					ENTITY_LINE_COLOR
				);
			}
		);
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

	private static void renderTransparentBox(
		PoseStack matrices,
		MultiBufferSource vertexConsumers,
		@NotNull AABB box,
		double cameraX, double cameraY, double cameraZ,
		double strength
	) {
		Vec3 vec3 = new Vec3(-cameraX, -cameraY, -cameraZ);
		DebugRenderer.renderFilledBox(
			matrices,
			vertexConsumers,
			box.move(vec3),
			(float) (1F - strength),
			(float) strength,
			0F,
			(float) (Math.max(0.1F, strength) * 0.4F)
		);
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
