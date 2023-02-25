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
import net.frozenblock.lib.config.api.client.option.OptionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Optional;

public class FrozenConfigScreen extends Screen {
	private final Screen parent;
	public final ClientConfig<?> config;
	private final Optional<ResourceLocation> backgroundTexture;

	public Button cancelButton;
	public Button saveButton;
	public OptionList optionList;

	public FrozenConfigScreen(ClientConfig<?> config, Screen parent) {
		this(config, parent, Optional.empty());
	}

	public FrozenConfigScreen(ClientConfig<?> config, Screen parent, Optional<ResourceLocation> backgroundTexture) {
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

		this.optionList = new OptionList(
				this.config.config(),
				this.minecraft,
				this.width,
				this.height,
				32,
				this.height - 32,
				25
		);

		this.addRenderableWidget(this.cancelButton);
		this.addRenderableWidget(this.saveButton);

		this.addWidget(this.optionList);

		this.config.init().accept(this);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		super.render(matrices, mouseX, mouseY, delta);
	}

	protected final boolean quit() {
		assert this.minecraft != null;
		this.minecraft.setScreen(this.parent);
		return true;
	}

	@Override
	public void onClose() {
		assert this.minecraft != null;
		this.quit();
	}
}
