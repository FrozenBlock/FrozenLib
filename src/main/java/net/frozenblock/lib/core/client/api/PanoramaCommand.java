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

package net.frozenblock.lib.core.client.api;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Environment(EnvType.CLIENT)
public class PanoramaCommand {

	public static void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(
			ClientCommandManager.literal("panorama")
				.executes(
					context -> {
						Minecraft client = Minecraft.getInstance();
						File directory = getPanoramaFolderName(new File(client.gameDirectory, "panoramas"));
						File directory1 = new File(directory, "screenshots");
						directory1.mkdir();
						directory1.mkdirs();
						client.grabPanoramixScreenshot(directory, 1024, 1024);
						return 1;
					}
				)
		);
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private static @NotNull File getPanoramaFolderName(File directory) {
		String string = DATE_FORMAT.format(new Date());
		int i = 1;
		while (true) {
			File file = new File(directory, string + (i == 1 ? "" : "_" + i));
			if (!file.exists()) {
				return file;
			}
			++i;
		}
	}
}
