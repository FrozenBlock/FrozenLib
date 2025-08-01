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

package net.frozenblock.lib.menu.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.menu.impl.SystemToastInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(SystemToast.class)
public abstract class SystemToastMixin implements SystemToastInterface {

	@Final
	@Shadow
	@Mutable
	private int width;

	@Shadow
	private List<FormattedCharSequence> messageLines;

	@Shadow
	public abstract void reset(Component component, @Nullable Component component2);

	@Unique
	@Override
	public void frozenLib$updateMultiline(
		SystemToast.SystemToastId systemToastId,
		Component header,
		@Nullable Component startAppend,
		@Nullable Component endAppend
	) {
		ArrayList<FormattedCharSequence> messageLines = new ArrayList<>(this.messageLines);
		if (startAppend != null) messageLines.addFirst(startAppend.getVisualOrderText());
		if (endAppend != null) messageLines.addLast(endAppend.getVisualOrderText());

		Font font = Minecraft.getInstance().font;
		int newWidth = Math.max(200, messageLines.stream().mapToInt(font::width).max().orElse(200)) + 30;

		this.reset(header, null);
		this.messageLines = List.copyOf(messageLines);
		this.width = newWidth;
	}
}
