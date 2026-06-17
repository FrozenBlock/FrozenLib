/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.core.client.api;

import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class PanoramaCommand {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(
			ClientCommands.literal("panorama")
				.executes(
					context -> {
						Minecraft client = Minecraft.getInstance();
						File directory = getPanoramaFolderName(new File(client.gameDirectory, "panoramas"));
						File directory1 = new File(directory, "screenshots");
						directory1.mkdir();
						directory1.mkdirs();
						client.grabPanoramixScreenshot(directory);
						return 1;
					}
				)
		);
	}

	private static File getPanoramaFolderName(File directory) {
		final String string = DATE_FORMAT.format(new Date());
		int fileIndex = 1;
		while (true) {
			File file = new File(directory, string + (fileIndex == 1 ? "" : "_" + fileIndex));
			if (!file.exists()) return file;
			++fileIndex;
		}
	}
}
