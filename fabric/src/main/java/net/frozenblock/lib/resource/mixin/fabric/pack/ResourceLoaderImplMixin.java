/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.resource.mixin.fabric.pack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.Optional;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.frozenblock.lib.resource.api.platform.ResourceLoaderHelperImpl;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResourceLoaderImpl.class)
public class ResourceLoaderImplMixin {

	@ModifyExpressionValue(
		method = "registerBuiltinResourcePacks",
		at = @At(
			value = "NEW",
			target = "(Ljava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/packs/repository/PackSource;Ljava/util/Optional;)Lnet/minecraft/server/packs/PackLocationInfo;"
		)
	)
	private static PackLocationInfo frozenLib$overrideBuiltInPackSource(PackLocationInfo original) {
		final Optional<PackSource> newPackSource = ResourceLoaderHelperImpl.getPackSourceOverride(original.id());
		if (newPackSource.isPresent()) {
			return new PackLocationInfo(
				original.id(),
				original.title(),
				newPackSource.get(),
				original.knownPackInfo()
			);
		}

		return original;
	}
}
