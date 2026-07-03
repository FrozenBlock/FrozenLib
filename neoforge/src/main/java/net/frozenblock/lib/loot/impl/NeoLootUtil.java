package net.frozenblock.lib.loot.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

public final class NeoLootUtil {
	public static final ThreadLocal<Map<Identifier, FrozenLibLootTableSource>> SOURCES = ThreadLocal.withInitial(HashMap::new);

	private NeoLootUtil() {
	}

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
