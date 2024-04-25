/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
		this.base.render(poseStack, vertexConsumers.getBuffer(RenderType.entityCutout(this.getTexture(entity))), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	public abstract ResourceLocation getTexture(T entity);

	public abstract ModelPart getRoot(Context ctx);
}
