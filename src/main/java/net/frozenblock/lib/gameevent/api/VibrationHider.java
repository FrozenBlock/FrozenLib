/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.gameevent.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

public class VibrationHider {

	public static final Map<Class<? extends VibrationSystem.User>, ShouldHideVibration> HIDDEN_VIBRATIONS = new Object2ObjectArrayMap<>();

	@FunctionalInterface
	public interface ShouldHideVibration {
		boolean shouldHideVibration(VibrationSystem.Data data, VibrationSystem.User user, ServerLevel level);
	}
}
