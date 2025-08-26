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

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DebugScreen extends Screen {
	public static final Component DEBUG = Component.translatable("menu.frozenlib.debug");
	protected @Nullable DebugRendererListWidget listWidget;

	public DebugScreen() {
		super(DEBUG);
	}

	@Override
	protected void init() {
		this.listWidget = this.addRenderableWidget(
			new DebugRendererListWidget(this.minecraft, this.width, this.height, 0, 22, this)
		);
	}

	private static Component getTranslatableForDebugRenderer(DebugRendererHolder debugRenderer) {
		ResourceLocation resourceLocation = DebugRenderManager.DEBUG_RENDERER_HOLDERS.get(debugRenderer);
		if (resourceLocation != null) {
			return Component.translatable("debug." + resourceLocation.getPath());
		}
		return Component.literal("UNKNOWN");
	}

	public static class DebugRendererListWidget extends ContainerObjectSelectionList<DebugRendererListWidget.Entry> {

		public DebugRendererListWidget(Minecraft minecraftClient, int width, int height, int y, int itemHeight, DebugScreen screen) {
			super(minecraftClient, width, height, y, itemHeight);
			DebugRenderManager.DEBUG_RENDERER_HOLDERS.keySet().forEach(
				(rendererHolder) -> {
					this.addEntry(
						new Entry(
							getTranslatableForDebugRenderer(rendererHolder),
							rendererHolder,
							screen
						)
					);
				}
			);
		}

		@Environment(EnvType.CLIENT)
		public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
			private final Component rendererName;
			private final Button button;
			private final Font font = Minecraft.getInstance().font;

			public Entry(Component rendererName, @NotNull DebugRendererHolder rendererHolder, @NotNull DebugScreen screen) {
				this.rendererName = rendererName;
				this.button = Button.builder(
					rendererHolder.getButtonComponent(),
					button -> {
						rendererHolder.toggle();
						button.setMessage(rendererHolder.getButtonComponent());
					}).width(80).pos((screen.width / 2 + 30), 0).build();
			}

			@Override
			public @NotNull List<? extends NarratableEntry> narratables() {
				return ImmutableList.of(this.button);
			}

			@Override
			public @NotNull List<? extends GuiEventListener> children() {
				return ImmutableList.of(this.button);
			}

			@Override
			public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				this.button.setY(this.getContentY());
				this.button.render(guiGraphics, mouseX, mouseY, tickDelta);
				guiGraphics.drawString(this.font, this.rendererName, this.getContentX(), this.getY() + 6, -1, true);
			}
		}
	}
}
