/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.menu.api;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class SplashTextAPI {
	private SplashTextAPI() {
	}

	private static final List<ResourceLocation> SPLASH_FILES = new ArrayList<>();
	private static final List<String> ADDITIONS = new ArrayList<>();
	private static final List<String> REMOVALS = new ArrayList<>();

	public static void addSplashLocation(ResourceLocation location) {
		SPLASH_FILES.add(location);
	}

	public static void add(String text) {
		ADDITIONS.add(text);
	}

	public static void remove(String text) {
		REMOVALS.add(text);
	}

	public static List<ResourceLocation> getSplashFiles() {
		return List.copyOf(SPLASH_FILES);
	}

	public static List<String> getAdditions() {
		return List.copyOf(ADDITIONS);
	}

	public static List<String> getRemovals() {
		return List.copyOf(REMOVALS);
	}
}
