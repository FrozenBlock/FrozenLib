package net.frozenblock.lib.debug.client.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public class DebugRendererHolder {
	private static final Component ENABLED = Component.translatable("option.frozenlib.true");
	private static final Component DISABLED = Component.literal("option.frozenlib.false");

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
