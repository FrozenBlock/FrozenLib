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

package net.frozenblock.lib.file.nbt;

import java.io.File;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class used to save and read NBT to/from files.
 */
public class NbtFileUtils {
	public static final File CONFIG_PATH = FabricLoader.getInstance().getConfigDir().toFile();

	/**
	 * Saves an NBT file to the config directory.
	 *
	 * @param compoundTag The {@link CompoundTag} to save.
	 * @param fileName The name to use for the file, excluding the file extension.
	 */
	public static void saveToConfigFile(CompoundTag compoundTag, String fileName) {
		CONFIG_PATH.mkdirs();
		saveToFile(compoundTag, new File(CONFIG_PATH, withNBTExtension(fileName)));
	}

	/**
	 * Saves an NBT file within a directory.
	 *
	 * @param compoundTag The {@link CompoundTag} to save.
	 * @param file The directory to save to.
	 * @param fileName The file name to save to, excluding the file extension.
	 */
	public static void saveToFile(CompoundTag compoundTag, @NotNull File file, String fileName) {
		file.mkdirs();
		File destFile = new File(file, withNBTExtension(fileName));
		saveToFile(compoundTag, destFile);
	}

	/**
	 * Saves an NBT file.
	 *
	 * @param compoundTag The {@link CompoundTag} to save.
	 * @param file The file to save to.
	 */
	public static void saveToFile(CompoundTag compoundTag, @NotNull File file) {
		file.getParentFile().mkdirs();
		try {
			NbtIo.writeCompressed(compoundTag, file.toPath());
		} catch (IOException iOException) {
			FrozenLibConstants.LOGGER.error("Could not save data {}", file, iOException);
		}
	}

	/**
	 * Reads an NBT file from the config directory.
	 *
	 * @param fileName The name of the file to read, excluding the file extension.
	 * @return a {@link CompoundTag} containing the NBT file's data.
	 */
	@Nullable
	public static CompoundTag readFromConfigFile(String fileName) {
		return readFromFile(new File(CONFIG_PATH, withNBTExtension(fileName)));
	}

	/**
	 * Reads an NBT file.
	 *
	 * @param file The path the NBT file is located in.
	 * @param fileName The name of the file to read, excluding the file extension.
	 * @return a {@link CompoundTag} containing the NBT file's data.
	 */
	@Nullable
	public static CompoundTag readFromFile(File file, String fileName) {
		return readFromFile(new File(file, withNBTExtension(fileName)));
	}

	/**
	 * Reads an NBT file.
	 *
	 * @param file The NBT file.
	 * @return a {@link CompoundTag} containing the NBT file's data.
	 */
	@Nullable
	public static CompoundTag readFromFile(@NotNull File file) {
		CompoundTag compoundTag = null;
		try {
			compoundTag = NbtIo.read(file.toPath());
		} catch (IOException iOException) {
			FrozenLibConstants.LOGGER.error("Could not read data {}", file, iOException);
		}
		return compoundTag;
	}

	/**
	 * Appends ".nbt" to the end of a {@link String}.
	 *
	 * @param string The file's name, excluding the file extension.
	 * @return The provided {@link String}, with ".nbt" appended at the end.
	 */
	@Contract(pure = true)
	public static @NotNull String withNBTExtension(String string) {
		return string + ".nbt";
	}
}
