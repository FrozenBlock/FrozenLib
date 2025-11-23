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

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

@UtilityClass
@Environment(EnvType.CLIENT)
public final class SplashTextAPI {
	private static final List<Identifier> SPLASH_FILES = new ArrayList<>();
	private static final List<String> ADDITIONS = new ArrayList<>();
	private static final List<String> REMOVALS = new ArrayList<>();

	public static void addSplashLocation(Identifier location) {
		SPLASH_FILES.add(location);
	}

	public static void add(String text) {
		ADDITIONS.add(text);
	}

	public static void remove(String text) {
		REMOVALS.add(text);
	}

	@Contract(pure = true)
	@Unmodifiable
	public static List<Identifier> getSplashFiles() {
		return List.copyOf(SPLASH_FILES);
	}

	@Contract(pure = true)
	@Unmodifiable
	public static List<String> getAdditions() {
		return List.copyOf(ADDITIONS);
	}

	@Contract(pure = true)
	@Unmodifiable
	public static List<String> getRemovals() {
		return List.copyOf(REMOVALS);
	}
}
