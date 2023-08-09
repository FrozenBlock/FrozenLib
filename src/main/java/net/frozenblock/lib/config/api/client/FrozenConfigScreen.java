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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.client.option.Option;
import net.frozenblock.lib.config.api.client.option.OptionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

	@SuppressWarnings({"unchecked", "rawtypes"})
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
					FrozenMain.log("Saving config from GUI", FrozenMain.UNSTABLE_LOGGING);
					for (var option : this.config.options()) {
						((Consumer) option.onValueUpdate()).accept(option.get());
					}
					this.config.save().run();
					this.quit();
				}
		).pos(
				this.width / 2 + 3,
				this.height - 26
		).size(
				buttonWidth,
				20
		).build();

		this.optionList = new OptionList(
				this.config.config(),
				this.minecraft,
				this.width,
				this.height,
				32,
				this.height - 32,
				25
		);

		this.optionList.addSmall(this.config.options().toArray(new Option<?>[0]));

		this.addRenderableWidget(this.cancelButton);
		this.addRenderableWidget(this.saveButton);

		this.addWidget(this.optionList);

		this.config.init().accept(this);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		this.optionList.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);

		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void renderDirtBackground(GuiGraphics graphics) {
		graphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
		var backgroundLocation = this.backgroundTexture.orElse(BACKGROUND_LOCATION);

		graphics.blit(backgroundLocation, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
