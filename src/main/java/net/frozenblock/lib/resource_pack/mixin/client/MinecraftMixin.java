/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.resource_pack.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.frozenblock.lib.resource_pack.impl.client.FrozenLibFolderRepositorySource;
import net.frozenblock.lib.resource_pack.impl.client.PackRepositoryInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Final
	private DirectoryValidator directoryValidator;

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "NEW",
			target = "([Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;"
		)
	)
	public PackRepository frozenLib$addFrozenLibRepositorySource(PackRepository original) {
		if (original instanceof PackRepositoryInterface packRepositoryInterface) {
			packRepositoryInterface.frozenLib$addRepositorySource(
				new FrozenLibFolderRepositorySource(
					FrozenLibModResourcePackApi.RESOURCE_PACK_DIRECTORY,
					PackType.CLIENT_RESOURCES,
					PackSource.BUILT_IN,
					this.directoryValidator,
					"frozenlib:"
				)
			);
			packRepositoryInterface.frozenLib$addRepositorySource(
				new FrozenLibFolderRepositorySource(
					FrozenLibModResourcePackApi.MOD_RESOURCE_PACK_DIRECTORY,
					PackType.CLIENT_RESOURCES,
					PackSource.BUILT_IN,
					this.directoryValidator,
					"frozenlib:mod/"
				)
			);
		} else if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			throw new AssertionError("BRUHHHH ITS NOT A FROZENLIB PACK REPOSITORY SOURCEEE BURHHHHHHGHGTY");
		}
		return original;
	}

}
