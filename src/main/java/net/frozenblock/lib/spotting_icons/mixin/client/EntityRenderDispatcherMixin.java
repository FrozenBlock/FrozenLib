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

package net.frozenblock.lib.spotting_icons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.frozenblock.lib.spotting_icons.impl.EntityRenderDispatcherWithIcon;
import net.frozenblock.lib.spotting_icons.impl.EntityRendererWithIcon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements EntityRenderDispatcherWithIcon {

	@Unique
	public <T extends Entity> void frozenLib$renderIcon(
		T entity, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight
	) {
		EntityRenderer<Entity, EntityRenderState> entityRenderer = (EntityRenderer<Entity, EntityRenderState>) this.getRenderer(entity);
		try {
			Vec3 vec3 = entityRenderer.getRenderOffset(entityRenderer.createRenderState(entity, partialTicks));
			double d = x + vec3.x();
			double e = y + vec3.y();
			double f = z + vec3.z();
			matrixStack.pushPose();
			matrixStack.translate(d, e, f);
			((EntityRendererWithIcon) entityRenderer).frozenLib$renderIcon(entity, rotationYaw, partialTicks, matrixStack, buffer, packedLight);
			matrixStack.popPose();
		} catch (Throwable throwable) {
			CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering entity icon in world");
			CrashReportCategory crashReportCategory = crashReport.addCategory("Entity icon being rendered");
			entity.fillCrashReportCategory(crashReportCategory);
			CrashReportCategory crashReportCategory2 = crashReport.addCategory("Renderer details");
			crashReportCategory2.setDetail("Assigned renderer", entityRenderer);
			crashReportCategory2.setDetail("Location", CrashReportCategory.formatLocation(entity.level(), x, y, z));
			crashReportCategory2.setDetail("Rotation", rotationYaw);
			crashReportCategory2.setDetail("Delta", partialTicks);
			throw new ReportedException(crashReport);
		}
	}

	@Shadow
	public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity) {
		throw new AssertionError("Mixin injection failed - FrozenLib EntityRenderDispatcherMixin");
	}

}
