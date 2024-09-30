/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.file.nbt;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NbtFileUtils {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final File CONFIG_PATH = FabricLoader.getInstance().getConfigDir().toFile();

	public static void saveToConfigFile(CompoundTag compoundTag, String fileName) {
		CONFIG_PATH.mkdirs();
		saveToFile(compoundTag, new File(CONFIG_PATH, withNBTExtension(fileName)));
	}

	public static void saveToFile(CompoundTag compoundTag, @NotNull File file, String fileName) {
		file.mkdirs();
		File destFile = new File(file, withNBTExtension(fileName));
		saveToFile(compoundTag, destFile);
	}

	public static void saveToFile(CompoundTag compoundTag, @NotNull File file) {
		file.mkdirs();
		try {
			NbtIo.writeCompressed(compoundTag, file.toPath());
		} catch (IOException iOException) {
			LOGGER.error("Could not save data {}", file, iOException);
		}
	}

	@Nullable
	public static CompoundTag readFromConfigFile(String fileName) {
		return readFromFile(new File(CONFIG_PATH, withNBTExtension(fileName)));
	}

	@Nullable
	public static CompoundTag readFromFile(File file, String fileName) {
		return readFromFile(new File(file, withNBTExtension(fileName)));
	}

	@Nullable
	public static CompoundTag readFromFile(@NotNull File file) {
		CompoundTag compoundTag = null;
		try {
			compoundTag = NbtIo.read(file.toPath());
		} catch (IOException iOException) {
			LOGGER.error("Could not read data {}", file, iOException);
		}
		return compoundTag;
	}

	@Contract(pure = true)
	public static @NotNull String withNBTExtension(String string) {
		return string + ".nbt";
	}

}
