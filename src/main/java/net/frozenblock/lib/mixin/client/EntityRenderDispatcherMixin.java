/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.frozenblock.lib.spotting_icons.impl.EntityRenderDispatcherWithIcon;
import net.frozenblock.lib.spotting_icons.impl.EntityRendererWithIcon;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements EntityRenderDispatcherWithIcon {

	@Shadow
	private Level level;

	@Unique
	public <E extends Entity> void renderIcon(E entity, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		EntityRenderer<E> entityRenderer = (EntityRenderer<E>) this.getRenderer(entity);
		try {
			Vec3 vec3 = entityRenderer.getRenderOffset(entity, partialTicks);
			double d = x + vec3.x();
			double e = y + vec3.y();
			double f = z + vec3.z();
			matrixStack.pushPose();
			matrixStack.translate(d, e, f);
			((EntityRendererWithIcon)entityRenderer).renderIcon(entity, rotationYaw, partialTicks, matrixStack, buffer, packedLight);
			matrixStack.popPose();
		}
		catch (Throwable throwable) {
			CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering entity in world");
			CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being rendered");
			entity.fillCrashReportCategory(crashReportCategory);
			CrashReportCategory crashReportCategory2 = crashReport.addCategory("Renderer details");
			crashReportCategory2.setDetail("Assigned renderer", entityRenderer);
			crashReportCategory2.setDetail("Location", CrashReportCategory.formatLocation(this.level, x, y, z));
			crashReportCategory2.setDetail("Rotation", Float.valueOf(rotationYaw));
			crashReportCategory2.setDetail("Delta", Float.valueOf(partialTicks));
			throw new ReportedException(crashReport);
		}
	}

	@Shadow public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
		throw new AssertionError("Mixin injection failed - FrozenLib EntityRenderDispatcherMixin");
	}

}
