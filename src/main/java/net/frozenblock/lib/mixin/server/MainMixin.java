/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.mixin.server;

import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoints.DataInitEntrypoint;
import net.minecraft.WorldVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.nio.file.Path;
import java.util.Collection;

@Mixin(Main.class)
public class MainMixin {

	@Inject(method = "createStandardGenerator", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/DataGenerator;getBuiltinDatapack(ZLjava/lang/String;)Lnet/minecraft/data/DataGenerator$PackGenerator;", ordinal = 0))
	private static void dataInit(Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, WorldVersion worldVersion, boolean bl, CallbackInfoReturnable<DataGenerator> cir) {
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:data_init", DataInitEntrypoint.class).forEach(entrypoint -> {
			try {
				DataInitEntrypoint mainPoint = entrypoint.getEntrypoint();
				mainPoint.init();
			} catch (Throwable ignored) {

			}
		});
	}
}
