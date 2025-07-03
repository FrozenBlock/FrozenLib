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

package net.frozenblock.lib.file.transfer;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

public class FileTransferFilter {
	private static final List<String> WHITELISTED_FILE_EXTENSIONS = ImmutableList.of("png", "json");
	private static final List<String> WHITELISTED_SERVER_DESTINATIONS = new ArrayList<>();
	private static final List<String> WHITELISTED_CLIENT_DESTINATIONS = new ArrayList<>();
	private static final List<String> WHITELISTED_SERVER_REQUEST_PATHS = new ArrayList<>();
	private static final List<String> WHITELISTED_CLIENT_REQUEST_PATHS = new ArrayList<>();

	public static boolean isTransferAcceptable(String destPath, String fileName, @Nullable ServerPlayer player) {
		boolean isServer = player != null;
		boolean returnValue = WHITELISTED_FILE_EXTENSIONS.contains(FilenameUtils.getExtension(fileName)) && isDestinationPathAcceptable(destPath, !isServer);

		if (!returnValue && isServer) {
			player.connection.disconnect(Component.translatable("frozenlib.file_transfer.unsupported_file"));
		}
		return returnValue;
	}

	public static boolean isRequestAcceptable(String requestPath, String fileName, @Nullable ServerPlayer player) {
		boolean isServer = player != null;
		boolean returnValue = WHITELISTED_FILE_EXTENSIONS.contains(FilenameUtils.getExtension(fileName)) && isRequestPathAcceptable(requestPath, !isServer);

		if (!returnValue && isServer) {
			player.connection.disconnect(Component.translatable("frozenlib.file_transfer.unsupported_file_request"));
		}
		return returnValue;
	}

	private static boolean isDestinationPathAcceptable(String destPath, boolean client) {
		return (client ? WHITELISTED_CLIENT_DESTINATIONS : WHITELISTED_SERVER_DESTINATIONS).contains(destPath);
	}

	private static boolean isRequestPathAcceptable(String destPath, boolean client) {
		return (client ? WHITELISTED_CLIENT_REQUEST_PATHS : WHITELISTED_SERVER_REQUEST_PATHS).contains(destPath);
	}

	public static void whitelistDestinationPath(String destPath, boolean client) {
		(client ? WHITELISTED_CLIENT_DESTINATIONS : WHITELISTED_SERVER_DESTINATIONS).add(destPath);
	}

	public static void whitelistRequestPath(String destPath, boolean client) {
		(client ? WHITELISTED_CLIENT_REQUEST_PATHS : WHITELISTED_SERVER_REQUEST_PATHS).add(destPath);
	}

}
