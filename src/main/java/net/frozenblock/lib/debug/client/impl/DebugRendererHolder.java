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

package net.frozenblock.lib.debug.client.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public class DebugRendererHolder {
	private static final Component ENABLED = Component.translatable("option.frozenlib.true");
	private static final Component DISABLED = Component.translatable("option.frozenlib.false");

	public final RenderInterface renderer;
	public boolean enabled = false;

	public DebugRendererHolder(RenderInterface renderer) {
		this.renderer = renderer;
	}

	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		if (this.enabled) {
			this.renderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
		}
	}

	public void toggle() {
		this.enabled = !this.enabled;
	}

	public Component getButtonComponent() {
		return this.enabled ? ENABLED : DISABLED;
	}

	@FunctionalInterface
	public interface RenderInterface {
		void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ);
	}
}
