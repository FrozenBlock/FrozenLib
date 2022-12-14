package net.frozenblock.lib.block.api.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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

@Environment(EnvType.CLIENT)
public abstract class BillboardBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final ModelPart base;

    public BillboardBlockEntityRenderer(Context ctx) {
        ModelPart root = this.getRoot();
        this.base = root.getChild("base");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) Math.PI, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 16, 16);
    }

	private final Quaternion rotation = new Quaternion(0F, 0F, 0F, 1F);

    public void render(@NotNull T entity, float tickDelta, @NotNull PoseStack poseStack, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
		this.rotation.set(0.0f, 0.0f, 0.0f, 1.0f);
		this.rotation.mul(Vector3f.YP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().yRot));
		poseStack.translate(0.5, 0, 0.5);
		poseStack.pushPose();
		poseStack.mulPose(this.rotation);
		this.base.render(poseStack, vertexConsumers.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity))), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
    }

	public abstract ResourceLocation getTextureLocation(T entity);

	public abstract ModelPart getRoot();
}
