package net.frozenblock.lib.config.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Optional;

public class FrozenConfigScreen extends Screen {
	private final Screen parent;
	public final ClientConfig config;
	private final Optional<ResourceLocation> backgroundTexture;

	public Button cancelButton;
	public Button saveButton;

	public FrozenConfigScreen(ClientConfig config, Screen parent) {
		this(config, parent, Optional.empty());
	}

	public FrozenConfigScreen(ClientConfig config, Screen parent, Optional<ResourceLocation> backgroundTexture) {
		super(config.title());
		this.config = config;
		this.parent = parent;
		this.backgroundTexture = backgroundTexture;
	}

	@Override
	public void init() {
		int buttonWidth = Math.min(200, (this.width - 50 - 12) / 3);

		this.cancelButton = new Button(
				this.width / 2 - buttonWidth - 3,
				this.height - 26,
				buttonWidth,
				20,
				Component.translatable("frozenlib.config.discard"),
				button -> this.quit()
		);

		this.saveButton = new Button(
				this.width / 2 + 3,
				this.height - 26,
				buttonWidth,
				20,
				Component.translatable("frozenlib.config.save"),
				button -> {
					this.config.save();
					this.quit();
				}
		);

		addRenderableWidget(this.cancelButton);
		addRenderableWidget(this.saveButton);

		this.config.init().accept(this);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		super.render(matrices, mouseX, mouseY, delta);
	}

	protected final boolean quit() {
		this.minecraft.setScreen(this.parent);
		return true;
	}

	@Override
	public void onClose() {
		assert this.minecraft != null;
		this.quit();
	}
}
