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

package net.frozenblock.lib.block.api.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public abstract class BillboardBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	private final ModelPart base;

	public BillboardBlockEntityRenderer(Context ctx) {
		ModelPart root = this.getRoot(ctx);
		this.base = root.getChild("base");
	}

	@NotNull
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) Math.PI, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 16, 16);
	}

	private final Quaternionf rotation = new Quaternionf(0F, 0F, 0F, 1F);

	@Override
	public void render(@NotNull T entity, float tickDelta, @NotNull PoseStack poseStack, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
		this.rotation.set(0.0f, 0.0f, 0.0f, 1.0f);
		this.rotation.mul(Axis.YP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().yRot));
		poseStack.translate(0.5, 0, 0.5);
		poseStack.pushPose();
		poseStack.mulPose(this.rotation);
		this.base.render(poseStack, vertexConsumers.getBuffer(RenderType.entityCutout(this.getTexture(entity))), light, overlay);
		poseStack.popPose();
	}

	public abstract ResourceLocation getTexture(T entity);

	public abstract ModelPart getRoot(Context ctx);
}
