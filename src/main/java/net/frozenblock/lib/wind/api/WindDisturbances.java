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
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.wind.impl.WindDisturbance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WindDisturbances {
	private static final List<Pair<Level, WindDisturbance>> WIND_DISTURBANCES_SERVER_A = new ArrayList<>();
	private static final List<Pair<Level, WindDisturbance>> WIND_DISTURBANCES_SERVER_B = new ArrayList<>();
	private static boolean isSwitchedServer;

	public static List<Pair<Level, WindDisturbance>> getWindDisturbancesServer() {
		return !isSwitchedServer ? WIND_DISTURBANCES_SERVER_A : WIND_DISTURBANCES_SERVER_B;
	}

	public static List<Pair<Level, WindDisturbance>> getAltListServer() {
		return isSwitchedServer ? WIND_DISTURBANCES_SERVER_A : WIND_DISTURBANCES_SERVER_B;
	}

	public static void clearServer() {
		getWindDisturbancesServer().clear();
	}

	public static void clearAllServer() {
		WIND_DISTURBANCES_SERVER_A.clear();
		WIND_DISTURBANCES_SERVER_B.clear();
	}

	public static void clearAndSwitchServer() {
		clearServer();
		isSwitchedServer = !isSwitchedServer;
	}

	private static final List<Pair<Level, WindDisturbance>> WIND_DISTURBANCES_CLIENT_A = new ArrayList<>();
	private static final List<Pair<Level, WindDisturbance>> WIND_DISTURBANCES_CLIENT_B = new ArrayList<>();
	private static boolean isSwitchedClient;

	public static List<Pair<Level, WindDisturbance>> getWindDisurbancesClient() {
		return !isSwitchedClient ? WIND_DISTURBANCES_CLIENT_A : WIND_DISTURBANCES_CLIENT_B;
	}

	public static List<Pair<Level, WindDisturbance>> getAltListClient() {
		return isSwitchedClient ? WIND_DISTURBANCES_CLIENT_A : WIND_DISTURBANCES_CLIENT_B;
	}

	public static void clearClient() {
		getWindDisurbancesClient().clear();
	}

	public static void clearAllClient() {
		WIND_DISTURBANCES_CLIENT_A.clear();
		WIND_DISTURBANCES_CLIENT_B.clear();
	}

	public static void clearAndSwitchClient() {
		clearClient();
		isSwitchedClient = !isSwitchedClient;
	}

	public static void addWindDisturbance(Level level, WindDisturbance windDisturbance) {
		getAltListServer().add(Pair.of(level, windDisturbance));
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			getAltListClient().add(Pair.of(level, windDisturbance));
		}
	}

	public static List<Pair<Level, WindDisturbance>> getWindDisturbances(@NotNull Level level) {
		if (!level.isClientSide) {
			return getWindDisturbancesServer();
		} else {
			return getWindDisurbancesClient();
		}
	}
}
