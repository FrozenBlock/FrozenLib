/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.file.transfer.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.ModLoader;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

@ClientOnly
public class FileTransferToast implements Toast {
	private static final Identifier BACKGROUND_SPRITE = FrozenLibConstants.id("toast/file_transfer");
	private static final int MAX_LINE_SIZE = 200;
	private static final int LINE_SPACING = 12;
	private static final int MARGIN = 10;
	private static final int DOUBLE_MARGIN = MARGIN * 2;
	private static final int WIDTH_BUFFER = MARGIN * 3;
	private final ToastId id;
	private final Component title;
	private final Optional<Supplier<Component>> bottomText;
	private final List<PathAndFileName> messageProviders = new ArrayList<>();
	private final List<FormattedCharSequence> messageLines = new ArrayList<>();
	private long lastChanged;
	private boolean changed;
	private int width = MAX_LINE_SIZE + WIDTH_BUFFER;
	private Visibility wantedVisibility;

	private static FileTransferToast create(
		ToastId id,
		PathAndFileName pathAndFileName
	) {
		final FileTransferToast toast = new FileTransferToast(id);
		toast.messageProviders.add(pathAndFileName);
		toast.updateTextAndWidth();
		return toast;
	}

	private FileTransferToast(ToastId id) {
		this.id = id;
		this.title = id.title;
		this.bottomText = id.bottomDisplay;
		this.wantedVisibility = Visibility.HIDE;
	}

	private void updateTextAndWidth() {
		final Stream<FormattedCharSequence> messages = this.messageProviders.stream()
			.map(provider -> provider.getComponent().getVisualOrderText());

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

	public void appendPathAndFileName(PathAndFileName pathAndFileName) {
		if (this.messageProviders.contains(pathAndFileName)) return;
		this.messageProviders.add(pathAndFileName);
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

	@Override
	public Visibility getWantedVisibility() {
		return this.wantedVisibility;
	}

	@Override
	public void update(ToastManager manager, long fullyVisibleForMs) {
		if ((fullyVisibleForMs % 100) == 0) this.updateTextAndWidth();

		if (this.changed) {
			this.lastChanged = fullyVisibleForMs;
			this.changed = false;
		}

		final double displayTime = this.id.displayTime * manager.getNotificationDisplayTimeMultiplier();
		final long timeSinceLastChanged = fullyVisibleForMs - this.lastChanged;
		this.wantedVisibility = timeSinceLastChanged < displayTime ? Visibility.SHOW : Visibility.HIDE;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
		if (this.messageLines.isEmpty()) {
			graphics.text(font, this.title, 15, 12, this.id.textColor(), false);
			return;
		}

		graphics.text(font, this.title, 15, 7, this.id.textColor(), false);
		for (int i = 0; i < this.messageLines.size(); ++i) {
			graphics.text(font, this.messageLines.get(i), 18, 18 + i * LINE_SPACING, -1, false);
		}
	}

	public void setChanged() {
		this.changed = true;
	}

	@Override
	public ToastId getToken() {
		return this.id;
	}

	public static void tryAdd(
		ToastManager toastManager,
		ToastId id,
		PathAndFileName pathAndFileName
	) {
		if (!id.debugOnly() || (FrozenLibConstants.DEBUG_FILE_TRANSFER || ModLoader.isDevelopmentEnvironment())) toastManager.addToast(create(id, pathAndFileName));
	}

	public static void addOrAppendIfNotPresent(
		ToastManager toastManager,
		ToastId id,
		String path,
		String fileName
	) {
		final PathAndFileName pathAndFileName = new PathAndFileName(path, fileName);
		final FileTransferToast packToast = toastManager.getToast(FileTransferToast.class, id);
		if (packToast == null) {
			tryAdd(toastManager, id, pathAndFileName);
			return;
		}

		packToast.appendPathAndFileName(pathAndFileName);
	}

	private static final class PathAndFileName {
		private final String path;
		private final String fileName;
		private final Component message;

		PathAndFileName(String path, String fileName) {
			this.path = path;
			this.fileName = fileName;
			this.message = Component.translatable("frozenlib.file_transfer.info", path, fileName);
		}

		public Component getComponent() {
			return this.message;
		}

		@Override
		public boolean equals(Object other) {
			return this == other
				|| (other instanceof PathAndFileName pathAndFileName
				&& this.path.equals(pathAndFileName.path)
				&& this.fileName.equals(pathAndFileName.fileName));
		}
	}

	public static class ToastId {
		private static final int COLOR_YELLOW = ARGB.color(255, 255, 0);
		private static final int COLOR_RED = ARGB.red(255);
		public static final ToastId TRANSFER_RECEIVE = new ToastId("frozenlib.file_transfer.transfer", COLOR_YELLOW, true);
		public static final ToastId REQUEST_RECEIVE = new ToastId("frozenlib.file_transfer.request", COLOR_YELLOW, true);
		public static final ToastId TRANSFER_FAIL = new ToastId("frozenlib.file_transfer.receive.fail", COLOR_YELLOW, true);
		public static final ToastId REQUEST_FAIL = new ToastId("frozenlib.file_transfer.request.fail", COLOR_YELLOW, true);
		public static final ToastId ILLEGAL = new ToastId(
			"frozenlib.file_transfer.illegal",
			Component.translatable("frozenlib.file_transfer.illegal.warn"),
			COLOR_RED,
			false
		);
		private final long displayTime;
		private final Component title;
		private final Optional<Supplier<Component>> bottomDisplay;
		private final int textColor;
		private final boolean debugOnly;

		public ToastId(long displayTime, String title, Optional<Supplier<Component>> bottomDisplay, int textColor, boolean debugOnly) {
			this.displayTime = displayTime;
			this.title = Component.translatable("frozenlib.resourcepack." + title);
			this.bottomDisplay = bottomDisplay;
			this.textColor = textColor;
			this.debugOnly = debugOnly;
		}

		public Component getTitle() {
			return this.title;
		}

		public Optional<Component> getBottomDisplay() {
			return this.bottomDisplay.map(Supplier::get);
		}

		public int textColor() {
			return this.textColor;
		}

		public boolean debugOnly() {
			return this.debugOnly;
		}

		public ToastId(String title, int textColor, boolean debugOnly) {
			this(5000L, title, Optional.empty(), textColor, debugOnly);
		}

		public ToastId(String title, Component component, int textColor, boolean debugOnly) {
			this(5000L, title, Optional.of(() -> component), textColor, debugOnly);
		}

		public ToastId(String title, Supplier<Component> supplier, int textColor, boolean debugOnly) {
			this(5000L, title, Optional.of(supplier), textColor, debugOnly);
		}
	}
}
