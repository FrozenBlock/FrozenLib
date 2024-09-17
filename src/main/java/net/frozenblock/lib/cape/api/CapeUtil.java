/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.cape.api;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.frozenblock.lib.cape.impl.Cape;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class CapeUtil {
	public static @NotNull @Unmodifiable List<Cape> getCapes() {
		return ImmutableList.copyOf(FrozenRegistry.CAPE);
	}

	public static @NotNull @Unmodifiable List<Cape> getUsableCapes(UUID uuid) {
		return ImmutableList.copyOf(getCapes().stream().filter(cape -> canPlayerUserCape(uuid, cape)).toList());
	}

	public static boolean canPlayerUserCape(UUID uuid, ResourceLocation capeID) {
		Optional<Cape> optionalCape = FrozenRegistry.CAPE.getOptional(capeID);
		return optionalCape.map(cape -> canPlayerUserCape(uuid, cape)).orElse(false);
	}

	public static boolean canPlayerUserCape(UUID uuid, @NotNull Cape cape) {
		return cape.allowedPlayers().map(uuids -> uuids.contains(uuid)).orElse(true);
	}

	public static @NotNull Cape registerCape(ResourceLocation id) {
		return Registry.register(FrozenRegistry.CAPE, id, new Cape(id, buildCapeTextureLocation(id), Optional.empty()));
	}

	public static @NotNull Cape registerCapeWithWhitelist(ResourceLocation id, List<UUID> allowedPlayers) {
		return Registry.register(FrozenRegistry.CAPE, id, new Cape(id, buildCapeTextureLocation(id), Optional.of(allowedPlayers)));
	}

	private static ResourceLocation buildCapeTextureLocation(@NotNull ResourceLocation cape) {
		return ResourceLocation.tryBuild(cape.getNamespace(), "textures/cape/" + cape.getPath() + ".png");
	}
}
