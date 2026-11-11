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

package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectors;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModifications;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class BiomeModificationTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends BiomeModification>> register = DeferredRegister.create(
			FrozenLibRegistries.BIOME_MODIFICATION_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("environment_attribute", () -> EnvironmentAttributeModification.CODEC);
		register.register("feature", () -> FeatureModification.CODEC);
		register.register("carver", () -> CarverModification.CODEC);

		register.register();

		// TODO: eventually fully data-drive modifications and tear down a ton of Fabric's impl
		for (ModificationPhase phase : ModificationPhase.values()) {
			BiomeModifications.create(FrozenLibConstants.id("dynamic_registry_modifications_at_" + phase.getSerializedName())).add(
				phase,
				BiomeSelectors.all(),
				(registryAccess, selectionContext, modificationContext) -> {
					registryAccess.lookupOrThrow(FrozenLibRegistries.BIOME_MODIFICATION).forEach(modification -> {
						if (!modification.selectorMatches(selectionContext)) return;
						modification.modify(modificationContext);
					});
				}
			);
		}
	}
}
