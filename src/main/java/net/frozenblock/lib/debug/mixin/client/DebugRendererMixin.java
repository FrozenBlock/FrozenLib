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

package net.frozenblock.lib.debug.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.debug.client.api.DebugRendererEvents;
import net.frozenblock.lib.debug.client.impl.DebugRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.LightSectionDebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.client.renderer.debug.RaidDebugRenderer;
import net.minecraft.client.renderer.debug.StructureRenderer;
import net.minecraft.client.renderer.debug.VillageSectionsDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
	@Shadow @Final
	public PathfindingRenderer pathfindingRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer waterDebugRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer heightMapRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer collisionBoxRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer supportBlockRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer neighborsUpdateRenderer;
	@Shadow @Final
	public StructureRenderer structureRenderer;
	@Shadow @Final
	public LightSectionDebugRenderer skyLightSectionDebugRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer solidFaceRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer chunkRenderer;
	@Shadow @Final
	public BrainDebugRenderer brainDebugRenderer;
	@Shadow @Final
	public VillageSectionsDebugRenderer villageSectionsDebugRenderer;
	@Shadow @Final
	public BeeDebugRenderer beeDebugRenderer;
	@Shadow @Final
	public RaidDebugRenderer raidDebugRenderer;
	@Shadow @Final
	public DebugRenderer.SimpleDebugRenderer lightDebugRenderer;

	@Inject(method = "render", at = @At("TAIL"))
	private void frozenLib$render(
		PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info
	) {
		if (FrozenLibConfig.IS_DEBUG) {
			DebugRenderManager.updatePartialTick();
			DebugRenderManager.DEBUG_RENDERER_HOLDERS.keySet().forEach((rendererEntry) -> rendererEntry.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ));
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void devtools$init(Minecraft client, CallbackInfo info) {
		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("pathfinding"),
			this.pathfindingRenderer::render);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("water_level"),
			this.waterDebugRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("heightmap"),
			this.heightMapRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("collision"),
			this.collisionBoxRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("support_block"),
			this.supportBlockRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("neighbor_update"),
			this.neighborsUpdateRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("structure"),
			this.structureRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("sky_light"),
			this.skyLightSectionDebugRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("solid_face"),
			this.solidFaceRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("chunk_status"),
			this.chunkRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("brain"),
			this.brainDebugRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("village_sections"),
			this.villageSectionsDebugRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("bee"),
			this.beeDebugRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("raid"),
			this.raidDebugRenderer::render
		);

		DebugRenderManager.registerRenderer(
			FrozenSharedConstants.id("light"),
			this.lightDebugRenderer::render
		);

		DebugRendererEvents.DEBUG_RENDERERS_CREATED.invoker().onDebugRenderersCreated(client);
	}

	@Inject(method = "clear", at = @At("TAIL"))
	private void devtools$clear(CallbackInfo info) {
		DebugRenderManager.clearAdditionalRenderers();
	}
}
