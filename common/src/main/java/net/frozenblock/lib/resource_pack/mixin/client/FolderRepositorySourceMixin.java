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

package net.frozenblock.lib.resource_pack.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.frozenblock.lib.resource_pack.impl.client.FrozenLibFolderRepositorySource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(FolderRepositorySource.class)
public class FolderRepositorySourceMixin {

	@WrapOperation(
		method = "createDiscoveredFilePackInfo",
		at = @At(
			value = "NEW",
			target = "(Ljava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/packs/repository/PackSource;Ljava/util/Optional;)Lnet/minecraft/server/packs/PackLocationInfo;"
		)
	)
	private PackLocationInfo frozenLib$modifyPackLocationInfo(String string, Component component, PackSource source, Optional optional, Operation<PackLocationInfo> original) {
		if (FolderRepositorySource.class.cast(this) instanceof FrozenLibFolderRepositorySource frozenLibFolderRepositorySource) {
			String componentString = string;
			if (componentString.endsWith(".zip")) componentString = componentString.substring(0, componentString.length() - 4);
			if (componentString.startsWith("file/")) componentString = componentString.substring(5);
			component = Component.translatable("frozenlib.resourcepack.pack." + componentString);

			string = frozenLibFolderRepositorySource.getSuffix() + string;
		}
		return original.call(string, component, source, optional);
	}

	@ModifyExpressionValue(
		method = "lambda$loadPacks$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/packs/repository/Pack;readMetaAndCreate(Lnet/minecraft/server/packs/PackLocationInfo;Lnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/server/packs/PackType;Lnet/minecraft/server/packs/PackSelectionConfig;)Lnet/minecraft/server/packs/repository/Pack;"
		)
	)
	private Pack frozenLib$denyLoadingOfUnregisteredPacks(
		Pack original,
		@Local(argsOnly = true) Path path
	) {
		if (!(FolderRepositorySource.class.cast(this) instanceof FrozenLibFolderRepositorySource frozenLibFolderRepositorySource) || original == null) return original;
		if (!frozenLibFolderRepositorySource.getSuffix().startsWith("frozenlib:mod/")) return original;

		final String packId = original.getId();
		if (FrozenLibModResourcePackApi.isFrozenLibPackRegisteredByMod(packId)) return original;

		path.toFile().delete();
		return null;
	}

}
