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
						FrozenSharedConstants.LOGGER.warn("PLAYER HAS ACCESS TO DEV CAMERA AND HAS JUST USED IT");
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
