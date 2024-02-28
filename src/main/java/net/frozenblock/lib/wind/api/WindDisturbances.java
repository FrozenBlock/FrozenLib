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

package net.frozenblock.lib.wind.api;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.wind.impl.InWorldWindModifier;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;

public class WindDisturbances {
	private static final List<Pair<Level, InWorldWindModifier>> IN_WORLD_WIND_MODIFIERS_SERVER_A = new ArrayList<>();
	private static final List<Pair<Level, InWorldWindModifier>> IN_WORLD_WIND_MODIFIERS_SERVER_B = new ArrayList<>();
	private static boolean isSwitchedServer;

	public static List<Pair<Level, InWorldWindModifier>> getInWorldWindModifiersServer() {
		return !isSwitchedServer ? IN_WORLD_WIND_MODIFIERS_SERVER_A : IN_WORLD_WIND_MODIFIERS_SERVER_B;
	}

	public static List<Pair<Level, InWorldWindModifier>> getAltListServer() {
		return isSwitchedServer ? IN_WORLD_WIND_MODIFIERS_SERVER_A : IN_WORLD_WIND_MODIFIERS_SERVER_B;
	}

	public static void clearServer() {
		getInWorldWindModifiersServer().clear();
	}

	public static void clearAllServer() {
		IN_WORLD_WIND_MODIFIERS_SERVER_A.clear();
		IN_WORLD_WIND_MODIFIERS_SERVER_B.clear();
	}

	public static void clearAndSwitchServer() {
		clearServer();
		isSwitchedServer = !isSwitchedServer;
	}

	private static final List<Pair<Level, InWorldWindModifier>> IN_WORLD_WIND_MODIFIERS_CLIENT_A = new ArrayList<>();
	private static final List<Pair<Level, InWorldWindModifier>> IN_WORLD_WIND_MODIFIERS_CLIENT_B = new ArrayList<>();
	private static boolean isSwitchedClient;

	public static List<Pair<Level, InWorldWindModifier>> getInWorldWindModifiersClient() {
		return !isSwitchedClient ? IN_WORLD_WIND_MODIFIERS_CLIENT_A : IN_WORLD_WIND_MODIFIERS_CLIENT_B;
	}

	public static List<Pair<Level, InWorldWindModifier>> getAltListClient() {
		return isSwitchedClient ? IN_WORLD_WIND_MODIFIERS_CLIENT_A : IN_WORLD_WIND_MODIFIERS_CLIENT_B;
	}

	public static void clearClient() {
		getInWorldWindModifiersClient().clear();
	}

	public static void clearAllClient() {
		IN_WORLD_WIND_MODIFIERS_CLIENT_A.clear();
		IN_WORLD_WIND_MODIFIERS_CLIENT_B.clear();
	}

	public static void clearAndSwitchClient() {
		clearClient();
		isSwitchedClient = !isSwitchedClient;
	}

	public static void addInWorldWindModifier(Level level, InWorldWindModifier inWorldWindModifier) {
		Pair<Level, InWorldWindModifier> pair = Pair.of(level, inWorldWindModifier);
		getAltListServer().add(pair);
		if (!FrozenNetworking.connectedToIntegratedServer()) {
			getAltListClient().add(pair);
		}
	}

	public static List<Pair<Level, InWorldWindModifier>> getInWorldWindModifiers() {
		if (FrozenNetworking.connectedToIntegratedServer() || FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			return getInWorldWindModifiersServer();
		} else {
			return getInWorldWindModifiersClient();
		}
	}
}
