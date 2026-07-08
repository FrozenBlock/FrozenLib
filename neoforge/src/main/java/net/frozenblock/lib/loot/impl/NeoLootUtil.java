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

package net.frozenblock.lib.loot.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class NeoLootUtil {
	public static final ThreadLocal<Map<Identifier, FrozenLibLootTableSource>> SOURCES = ThreadLocal.withInitial(HashMap::new);

	public static FrozenLibLootTableSource determineSource(@Nullable Resource resource) {
		if (resource != null) {
			PackLocationInfo location = resource.source().location();

			if (location.source() == PackSource.BUILT_IN) {
				return FrozenLibLootTableSource.VANILLA;
			}

			Optional<KnownPack> knownPack = location.knownPackInfo();
			// TODO test
			if (knownPack.isPresent() && "neoforge".equals(knownPack.get().namespace())) {
				return FrozenLibLootTableSource.MOD;
			}
		}

		// If not vanilla or a mod's own bundled pack, assume external data pack.
		return FrozenLibLootTableSource.DATA_PACK;
	}
}
