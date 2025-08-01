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

package net.frozenblock.lib.menu.api;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.menu.impl.SystemToastInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@Environment(EnvType.CLIENT)
public final class SystemToastUpdater {

	public static void addOrUpdateMultiLine(
		@NotNull ToastComponent toastComponent,
		SystemToast.SystemToastId systemToastId,
		Component header,
		@NotNull Component text,
		@Nullable Component startAppend,
		@Nullable Component endAppend
	) {
		SystemToast systemToast = toastComponent.getToast(SystemToast.class, systemToastId);
		if (systemToast == null) {
			addMultiline(toastComponent, systemToastId, header, text);
			return;
		}
		((SystemToastInterface)systemToast).frozenLib$updateMultiline(systemToastId, header, startAppend, endAppend);
	}

	public static void addMultiline(
		@NotNull ToastComponent toastComponent,
		SystemToast.SystemToastId systemToastId,
		Component header,
		@NotNull Component text
	) {
		toastComponent.addToast(SystemToast.multiline(Minecraft.getInstance(), systemToastId, header, text));
	}

	public static void updateMultiLine(
		@NotNull ToastComponent toastComponent,
		SystemToast.SystemToastId systemToastId,
		Component header,
		@Nullable Component startAppend,
		@Nullable Component endAppend
	) {
		SystemToast systemToast = toastComponent.getToast(SystemToast.class, systemToastId);
		if (systemToast == null) return;
		((SystemToastInterface)systemToast).frozenLib$updateMultiline(systemToastId, header, startAppend, endAppend);
	}

	public static boolean doesToastOfTypeExist(@NotNull ToastComponent toastComponent, SystemToast.SystemToastId systemToastId) {
		return toastComponent.getToast(SystemToast.class, systemToastId) != null;
	}
}
