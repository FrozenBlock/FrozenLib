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

package net.frozenblock.lib.particle.api;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class VibrationParticleVisibilityApi {
	private static final List<VibrationParticleVisibilityTest> VISIBILITY_TESTS = new ArrayList<>();

	public static void registerVisibilityTest(@NotNull VibrationParticleVisibilityTest test) {
		VISIBILITY_TESTS.add(test);
	}

	public static boolean isVisible(@NotNull VibrationSystem.Data data, @NotNull VibrationSystem.User user) {
		for (VibrationParticleVisibilityTest test : VISIBILITY_TESTS) {
			if (!test.test(data, user)) return false;
		}
		return true;
	}

	@FunctionalInterface
	public interface VibrationParticleVisibilityTest {
		boolean test(VibrationSystem.Data data, VibrationSystem.User user);
	}
}
