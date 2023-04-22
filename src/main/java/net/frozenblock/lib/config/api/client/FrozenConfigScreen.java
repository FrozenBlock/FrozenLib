/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
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

		this.cancelButton = Button.builder(
				Component.translatable("frozenlib.config.discard"),
				button -> this.quit()
		).pos(
				this.width / 2 - buttonWidth - 3,
				this.height - 26
		).size(
				buttonWidth,
				20
		).build();

		this.saveButton = Button.builder(
				Component.translatable("frozenlib.config.save"),
				button -> {
					this.config.save();
					this.quit();
				}
		).pos(
				this.width / 2 + 3,
				this.height - 26
		).size(
				buttonWidth,
				20
		).build();

		addRenderableWidget(this.cancelButton);
		addRenderableWidget(this.saveButton);

		this.config.init().accept(this);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		renderBackground(guiGraphics);

		super.render(guiGraphics, mouseX, mouseY, delta);
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
