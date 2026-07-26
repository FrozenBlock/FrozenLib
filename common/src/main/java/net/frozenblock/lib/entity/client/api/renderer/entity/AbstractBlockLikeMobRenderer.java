/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.entity.client.api.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entity.api.AbstractBlockLikeMob;
import net.frozenblock.lib.entity.client.api.renderer.entity.state.BlockLikeMobRenderState;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@ClientOnly
public abstract class AbstractBlockLikeMobRenderer<T extends AbstractBlockLikeMob, S extends BlockLikeMobRenderState, M extends EntityModel<? super S>> extends MobRenderer<T, S, M> {
	private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	private static final Identifier EMPTY_TEXTURE = FrozenLibConstants.id("empty");
	protected final BlockModelResolver blockModelResolver;

	public AbstractBlockLikeMobRenderer(Context context, M model) {
		super(context, model, 0F);
		this.blockModelResolver = context.getBlockModelResolver();
	}

	@Override
	protected float getShadowRadius(S renderState) {
		return renderState.boundingBoxWidth / 1.65F;
	}

	@Override
	public void submit(S renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		final float scale = renderState.scale;
		poseStack.scale(scale, scale, scale);
		applyRotation(poseStack, renderState.rotationPivot, renderState.rotation);

		if (!renderState.blockState.isAir()) {
			final int overlayCoords = getOverlayCoords(renderState, this.getWhiteOverlayProgress(renderState));
			renderState.blockModel.submit(poseStack, submitNodeCollector, renderState.lightCoords, overlayCoords, renderState.outlineColor);
		}

		this.submitExtras(renderState, poseStack, submitNodeCollector, camera);
		poseStack.popPose();

		super.submit(renderState, poseStack, submitNodeCollector, camera);
	}

	protected abstract void submitExtras(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera);

	@Override
	public void extractRenderState(T entity, S renderState, float partialTicks) {
		super.extractRenderState(entity, renderState, partialTicks);
		renderState.blockState = entity.getBlockState();
		if (!renderState.blockState.isAir()) this.blockModelResolver.update(renderState.blockModel, renderState.blockState, BLOCK_DISPLAY_CONTEXT);
		extractRotation(entity, renderState, partialTicks);
	}

	public static void applyRotation(PoseStack poseStack, Vec3 rotationPivot, Quaternionf rotation) {
		poseStack.mulPose(rotation);
		poseStack.translate(rotationPivot);
	}

	private static Vector3f[] getCorners(float sizeX, float sizeY, float sizeZ) {
		return new Vector3f[]{
			new Vector3f(-sizeX, -sizeY, -sizeZ),
			new Vector3f(-sizeX, -sizeY, sizeZ),
			new Vector3f(-sizeX, sizeY, -sizeZ),
			new Vector3f(-sizeX, sizeY, sizeZ),
			new Vector3f(sizeX, -sizeY, -sizeZ),
			new Vector3f(sizeX, -sizeY, sizeZ),
			new Vector3f(sizeX, sizeY, -sizeZ),
			new Vector3f(sizeX, sizeY, sizeZ)
		};
	}

	public void extractRotation(T entity, S renderState, float partialTicks) {
		entity.getRotation(renderState.rotation, partialTicks);
		renderState.rotation.x = -renderState.rotation.x;
		renderState.rotation.w = -renderState.rotation.w;

		final AABB box = entity.getBoundingBox();
		final AABB.Builder builder = new AABB.Builder();
		final double sizeX = box.getXsize();
		final double sizeY = box.getYsize();
		final double sizeZ = box.getZsize();
		for (Vector3f corner : getCorners((float)sizeX, (float)sizeY, (float)sizeZ)) {
			renderState.rotation.transformUnit(corner);
			builder.include(corner);
		}

		final Direction direction = entity.getClimbingDirection();
		//final Direction direction = Direction.getApproximateNearest(entity.getDeltaMovement());
		final Direction.Axis axis = direction.getAxis();
		final AABB extents = builder.build();
		final double edgeOffset = AdvancedMath.edge(extents, direction);
		if (axis == Direction.Axis.Y) {
			renderState.y -= edgeOffset * 0.5D;
		} else {
			final double sideLength = axis.choose(sizeX, 0D, sizeZ);
			final double offset = (edgeOffset - sideLength * direction.getAxisDirection().getStep()) * 0.5D;
			renderState.y += sizeY * 0.5D;
			if (axis == Direction.Axis.X) {
				renderState.x -= offset;
			} else {
				renderState.z -= offset;
			}
		}

		renderState.rotationPivot = entity.getBoundingBoxOffset();
	}

	@Nullable
	@Override
	public Identifier getTextureLocation(S state) {
		return EMPTY_TEXTURE;
	}

	@Nullable
	@Override
	protected RenderType getRenderType(S state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
		return null;
	}
}
