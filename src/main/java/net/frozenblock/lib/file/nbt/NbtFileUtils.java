/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.file.nbt;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
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
	public static CompoundTag readFromFile(File file) {
		CompoundTag compoundTag = null;
		try {
			compoundTag = NbtIo.read(file.toPath());
		} catch (IOException iOException) {
			LOGGER.error("Could not read data {}", file, iOException);
		}
		return compoundTag;
	}

	public static String withNBTExtension(String string) {
		return string + ".nbt";
	}

}
