/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.config.newconfig.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TestConfigScreen extends OptionsSubScreen {
	private static final Component TITLE = Component.translatable("controls.keybinds.title");
	@Nullable
	public KeyMapping selectedKey;
	public long lastKeySelection;
	private ConfigEntriesList configEntriesList;
	private Button resetButton;

	public TestConfigScreen(final Screen lastScreen, final Options options) {
		super(lastScreen, options, TITLE);
	}

	@Override
	protected void addContents() {
		this.configEntriesList = this.layout.addToContents(new ConfigEntriesList(this, this.minecraft));
	}

	@Override
	protected void addOptions() {
	}

	@Override
	protected void addFooter() {
		this.resetButton = Button.builder(Component.translatable("controls.resetAll"), button -> {
			for (KeyMapping key : this.options.keyMappings) key.setKey(key.getDefaultKey());

			this.configEntriesList.resetMappingAndUpdateButtons();
		}).build();
		LinearLayout bottomButtons = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
		bottomButtons.addChild(this.resetButton);
		bottomButtons.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).build());
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
		this.configEntriesList.updateSize(this.width, this.layout);
	}

	@Override
	public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
		if (this.selectedKey != null) {
			this.selectedKey.setKey(InputConstants.Type.MOUSE.getOrCreate(event.button()));
			this.selectedKey = null;
			this.configEntriesList.resetMappingAndUpdateButtons();
			return true;
		} else {
			return super.mouseClicked(event, doubleClick);
		}
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		if (this.selectedKey != null) {
			if (event.isEscape()) {
				this.selectedKey.setKey(InputConstants.UNKNOWN);
			} else {
				this.selectedKey.setKey(InputConstants.getKey(event));
			}

			this.selectedKey = null;
			this.lastKeySelection = Util.getMillis();
			this.configEntriesList.resetMappingAndUpdateButtons();
			return true;
		} else {
			return super.keyPressed(event);
		}
	}

	@Override
	public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
		super.render(graphics, mouseX, mouseY, a);
		boolean canReset = false;

		for (KeyMapping key : this.options.keyMappings) {
			if (!key.isDefault()) {
				canReset = true;
				break;
			}
		}

		this.resetButton.active = canReset;
	}
}
