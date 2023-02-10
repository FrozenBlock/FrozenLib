package net.frozenblock.lib.config.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FrozenConfigScreen extends Screen {
	private final Screen parent;
	public final ClientConfig config;

	public Button saveButton;

	public Component saveButtonText;
	public Component saveButtonTooltip;

	public FrozenConfigScreen(ClientConfig config, Screen parent) {
		super(config.title());
		this.config = config;
		this.parent = parent;
	}

	@Override
	public void init() {
		int columnWidth = this.width / 3;
		int padding = columnWidth / 20;
		columnWidth = Math.min(columnWidth, 400);
		int paddedWidth = columnWidth - padding * 2;

		this.saveButton = new Button(
				0,
				0,
				200,
				20,
				Component.translatable("frozenlib.config.save"),
				button -> {}
		);

		addRenderableWidget(this.saveButton);

		this.config.init().accept(this);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void updateSaveButton(Component text, Component tooltip) {
		this.saveButtonText = text;
		this.saveButtonTooltip = tooltip;
	}

	@Override
	public void onClose() {
		assert this.minecraft != null;
		this.minecraft.setScreen(this.parent);
	}
}
