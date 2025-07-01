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

package net.frozenblock.lib.resource_pack.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.resource_pack.impl.FrozenLibFolderRepositorySource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Mixin(FolderRepositorySource.class)
public class FolderRepositorySourceMixin {

	@WrapOperation(
		method = "createDiscoveredFilePackInfo",
		at = @At(
			value = "NEW",
			target = "(Ljava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/packs/repository/PackSource;Ljava/util/Optional;)Lnet/minecraft/server/packs/PackLocationInfo;"
		)
	)
	private PackLocationInfo frozenLib$modifyPackLocationInfo(String string, Component component, PackSource packSource, Optional optional, Operation<PackLocationInfo> original) {
		if (FolderRepositorySource.class.cast(this) instanceof FrozenLibFolderRepositorySource) {
			String componentString = string;
			if (componentString.endsWith(".zip")) componentString = componentString.substring(0, componentString.length() - 4);
			if (componentString.startsWith("file/")) componentString = componentString.substring(5);
			component = Component.translatable("frozenlib.resourcepack." + componentString);

			string = "frozenlib/" + string;
		}
		return original.call(string, component, packSource, optional);
	}

}
