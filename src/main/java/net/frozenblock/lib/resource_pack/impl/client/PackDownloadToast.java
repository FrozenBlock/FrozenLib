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

package net.frozenblock.lib.resource_pack.impl.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

@Environment(EnvType.CLIENT)
public class PackDownloadToast implements Toast {
	private static final Identifier BACKGROUND_SPRITE = FrozenLibConstants.id("toast/resource_pack");
	private static final int MAX_LINE_SIZE = 200;
	private static final int LINE_SPACING = 12;
	private static final int MARGIN = 10;
	private static final int DOUBLE_MARGIN = MARGIN * 2;
	private static final int WIDTH_BUFFER = MARGIN * 3;
	private final PackDownloadToast.PackDownloadToastId id;
	private Component title;
	private final Optional<Supplier<Component>> bottomText;
	private final List<FrozenLibModResourcePackApi.PackDownloadStatusProvider> messageProviders = new ArrayList<>();
	private final List<FormattedCharSequence> messageLines = new ArrayList<>();
	private long lastChanged;
	private boolean changed;
	private int width = MAX_LINE_SIZE + WIDTH_BUFFER;
	private boolean forceHide;
	private Toast.Visibility wantedVisibility;

	public static PackDownloadToast create(
		PackDownloadToast.PackDownloadToastId id,
		FrozenLibModResourcePackApi.PackDownloadStatusProvider statusProvider
	) {
		final PackDownloadToast toast = new PackDownloadToast(id);
		toast.messageProviders.add(statusProvider);
		toast.updateTextAndWidth();
		return toast;
	}

	private PackDownloadToast(PackDownloadToast.PackDownloadToastId id) {
		this.id = id;
		this.title = id.title;
		this.bottomText = id.bottomDisplay;
		this.wantedVisibility = Visibility.HIDE;
	}

	private void updateTextAndWidth() {
		final Stream<FormattedCharSequence> messages = this.messageProviders.stream()
			.map(provider -> provider.getComponent(this.id).getVisualOrderText());

		this.messageLines.clear();
		this.messageLines.addAll(messages.toList());
		this.bottomText.ifPresent(supplier -> this.messageLines.add(supplier.get().getVisualOrderText()));

		final Font font = Minecraft.getInstance().font;
		final List<Integer> allLines = new ArrayList<>();
		allLines.add(WIDTH_BUFFER + font.width(this.title));
		this.bottomText.ifPresent(supplier -> allLines.add(WIDTH_BUFFER + font.width(supplier.get())));
		this.messageLines.forEach(line -> allLines.add(font.width(line)));

		this.width = Math.max(MAX_LINE_SIZE, allLines.stream().mapToInt(Integer::intValue).max().orElse(MAX_LINE_SIZE));
	}

	public void appendStatusProvider(FrozenLibModResourcePackApi.PackDownloadStatusProvider provider) {
		if (this.messageProviders.contains(provider)) return;
		this.messageProviders.add(provider);
		this.updateTextAndWidth();
		this.setChanged();
	}

	@Override
	public int width() {
		return this.width;
	}

	@Override
	public int height() {
		return DOUBLE_MARGIN + Math.max(this.messageLines.size(), 1) * LINE_SPACING;
	}

	public void forceHide() {
		this.forceHide = true;
	}

	@Override
	public Toast.Visibility getWantedVisibility() {
		return this.wantedVisibility;
	}

	@Override
	public void update(ToastManager toastManager, long time) {
		if ((time % 100) == 0) this.updateTextAndWidth();

		if (this.changed) {
			this.lastChanged = time;
			this.changed = false;
		}

		final double displayTime = this.id.displayTime * toastManager.getNotificationDisplayTimeMultiplier();
		final long timeSinceLastChanged = time - this.lastChanged;
		this.wantedVisibility = !this.forceHide && timeSinceLastChanged < displayTime ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	@Override
	public void render(GuiGraphics guiGraphics, Font font, long l) {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
		if (this.messageLines.isEmpty()) {
			guiGraphics.drawString(font, this.title, 15, 12, -256, false);
			return;
		}

		guiGraphics.drawString(font, this.title, 15, 7, -256, false);
		for (int i = 0; i < this.messageLines.size(); ++i) {
			guiGraphics.drawString(font, this.messageLines.get(i), 18, 18 + i * LINE_SPACING, -1, false);
		}
	}

	public void setChanged() {
		this.changed = true;
	}

	@Override
	public PackDownloadToast.PackDownloadToastId getToken() {
		return this.id;
	}

	public static void add(
		ToastManager toastManager,
		PackDownloadToast.PackDownloadToastId id,
		FrozenLibModResourcePackApi.PackDownloadStatusProvider statusProvider
	) {
		toastManager.addToast(create(id, statusProvider.getDirectProvider()));
	}

	public static void addOrAppendIfNotPresent(
		ToastManager toastManager,
		PackDownloadToast.PackDownloadToastId id,
		FrozenLibModResourcePackApi.PackDownloadStatusProvider statusProvider
	) {
		final FrozenLibModResourcePackApi.PackDownloadStatusProvider directProvider = statusProvider.getDirectProvider();
		final PackDownloadToast packToast = toastManager.getToast(PackDownloadToast.class, id);
		if (packToast == null) {
			add(toastManager, id, directProvider);
			return;
		}

		packToast.appendStatusProvider(directProvider);
	}

	public static void forceHide(ToastManager toastManager, PackDownloadToast.PackDownloadToastId id) {
		final PackDownloadToast PackDownloadToast = toastManager.getToast(PackDownloadToast.class, id);
		if (PackDownloadToast != null) PackDownloadToast.forceHide();
	}

	public static class PackDownloadToastId {
		public static final PackDownloadToastId PACK_DOWNLOAD_SUCCESS = new PackDownloadToastId(
			"download.success",
			() -> FrozenClientNetworking.notConnected()
				? Component.translatable("frozenlib.resourcepack.download.open_menu")
				: Component.translatable("frozenlib.resourcepack.download.press_f3")
		);
		public static final PackDownloadToastId PACK_UPDATE_SUCCESS = new PackDownloadToastId(
			"download.success.update",
			() -> FrozenClientNetworking.notConnected()
				? Component.translatable("frozenlib.resourcepack.download.open_menu")
				: Component.translatable("frozenlib.resourcepack.download.press_f3")
		);
		public static final PackDownloadToastId PACK_DOWNLOAD_FAILURE = new PackDownloadToastId("download.failure");
		public static final PackDownloadToastId PACK_DOWNLOAD_FAILURE_PRESENT = new PackDownloadToastId("download.failure.present");
		public static final PackDownloadToastId PACK_DOWNLOAD_PRESENT = new PackDownloadToastId("download.present");
		private final long displayTime;
		private final Component title;
		private final Optional<Supplier<Component>> bottomDisplay;

		public PackDownloadToastId(long displayTime, String title, Optional<Supplier<Component>> bottomDisplay) {
			this.displayTime = displayTime;
			this.title = Component.translatable("frozenlib.resourcepack." + title);
			this.bottomDisplay = bottomDisplay;
		}

		public Component getTitle() {
			return this.title;
		}

		public Optional<Component> getBottomDisplay() {
			return this.bottomDisplay.map(Supplier::get);
		}

		public PackDownloadToastId(String title) {
			this(5000L, title, Optional.empty());
		}

		public PackDownloadToastId(String title, Component component) {
			this(5000L, title, Optional.of(() -> component));
		}

		public PackDownloadToastId(String title, Supplier<Component> supplier) {
			this(5000L, title, Optional.of(supplier));
		}
	}
}
