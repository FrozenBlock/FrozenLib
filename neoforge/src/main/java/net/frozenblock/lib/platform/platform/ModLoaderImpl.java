/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.platform.platform;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.platform.api.Env;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;

public class ModLoaderImpl {
	private static final Map<Path, FileSystem> MOD_JAR_FILESYSTEMS = new ConcurrentHashMap<>();

	private static FileSystem openOrGetFileSystem(Path jarPath) {
		return MOD_JAR_FILESYSTEMS.computeIfAbsent(jarPath, p -> {
			try {
				return FileSystems.newFileSystem(p);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	public static boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	public static Path getGameDir() {
		return FMLLoader.getCurrent().getGameDir();
	}

	public static Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isModLoaded(String modId) {
		final var modList = ModList.get();
		return modList != null && modList.isLoaded(modId);
	}

	public static boolean isFabric() {
		return false;
	}

	public static boolean isNeoForge() {
		return true;
	}

	public static @Nullable <T> T ifFabric(Supplier<T> supplier) {
		return null;
	}

	public static @Nullable <T> T ifNeoForge(Supplier<T> supplier) {
		return supplier.get();
	}

	public static boolean isClient() {
		return FMLLoader.getCurrent().getDist().isClient();
	}

	public static boolean isServer() {
		return FMLLoader.getCurrent().getDist().isDedicatedServer();
	}

	public static Env getEnv() {
		return switch (FMLLoader.getCurrent().getDist()) {
			case CLIENT -> Env.CLIENT;
			case DEDICATED_SERVER -> Env.SERVER;
		};
	}

	public static String[] getLaunchArgs() {
		return FMLLoader.getCurrent().getProgramArgs().getArguments();
	}

	public static List<ModLoader.ModEntry> getAllMods() {
		final ModList modList = ModList.get();
		if (modList == null) return List.of();

		final List<ModLoader.ModEntry> result = new ArrayList<>();
		for (var modInfo : modList.getMods()) {
			result.add(new ModLoader.ModEntry() {
				@Override
				public String getId() {
					return modInfo.getModId();
				}

				@Override
				public String getName() {
					return modInfo.getDisplayName();
				}

				@Override
				public Optional<JsonElement> getCustomData(String key) {
					return Optional.empty();
				}

				@Override
				public Optional<Path> findPath(String file) {
					final var contents = modInfo.getOwningFile().getFile().getContents();
					if (!contents.containsFile(file)) return Optional.empty();

					final Path primary = contents.getPrimaryPath();
					try {
						if (Files.isDirectory(primary)) return Optional.of(primary.resolve(file));

						final FileSystem fs = openOrGetFileSystem(primary);
						final Path result = fs.getPath("/" + file);
						return Files.exists(result) ? Optional.of(result) : Optional.empty();
					} catch (Exception e) {
						return Optional.empty();
					}
				}

				@Override
				public Collection<Path> getRootPaths() {
					final var contents = modInfo.getOwningFile().getFile().getContents();
					final Path primary = contents.getPrimaryPath();
					try {
						if (Files.isDirectory(primary)) return List.of(primary);

						final FileSystem fs = openOrGetFileSystem(primary);
						final List<Path> roots = new ArrayList<>();
						fs.getRootDirectories().forEach(roots::add);
						return List.copyOf(roots);
					} catch (Exception e) {
						return List.of();
					}
				}
			});
		}
		return result;
	}
}
