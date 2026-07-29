/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.resource.mixin.client.pack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.lib.resource.client.api.pack.FrozenLibFolderRepositorySource;
import net.frozenblock.lib.resource.client.impl.pack.PackRepositoryInterface;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Final
	private DirectoryValidator directoryValidator;

	/**
	 * NeoForge has a separate event for adding {@link PackRepository}s.
	 */
	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "NEW",
			target = "([Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;"
		)
	)
	public PackRepository frozenLib$addFrozenLibRepositorySource(PackRepository original) {
		if (!(original instanceof PackRepositoryInterface packRepositoryInterface)) {
			if (ModLoader.isDevelopmentEnvironment()) throw new AssertionError("BRUHHHH ITS NOT A FROZENLIB PACK REPOSITORY SOURCEEE BURHHHHHHGHGTY");
			return original;
		}

		FrozenLibFolderRepositorySource.createDefaultSources(this.directoryValidator).forEach(packRepositoryInterface::frozenLib$addRepositorySource);

		return original;
	}
}
