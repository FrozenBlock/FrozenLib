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
import net.frozenblock.lib.resource_pack.impl.FrozenLibFolderRepositorySource;
import net.frozenblock.lib.resource_pack.impl.PackRepositoryInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.io.File;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Final
	public File gameDirectory;

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
		Path frozenLibResourcePackPath = this.gameDirectory.toPath().resolve("frozenlib").resolve("resourcepacks");
		RepositorySource frozenLibRepositorySource = new FrozenLibFolderRepositorySource(
			frozenLibResourcePackPath, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN, this.directoryValidator
		);
		if (original instanceof PackRepositoryInterface packRepositoryInterface) {
			packRepositoryInterface.frozenLib$addRepositorySource(frozenLibRepositorySource);
		} else if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			throw new AssertionError("BRUHHHH ITS NOT A FROZENLIB PACK REPOSITORY SOURCEEE BURHHHHHHGHGTY");
		}
		return original;
	}

}
