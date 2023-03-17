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

package net.frozenblock.lib.math.test.command;

import com.mojang.brigadier.CommandDispatcher;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class NoiseTestCommand { //Used to profile noises

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("noisetest").requires(source -> source.hasPermission(2)).executes(context -> noiseTest(context.getSource())));
	}

	private static int noiseTest(CommandSourceStack source) {
		EasyNoiseSampler.sampleTest();
		source.sendSuccess(Component.literal("CHECK LOGS FOR NOISE SPEEDS"), true);
		return 1;
	}

}
