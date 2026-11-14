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
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class BiomeModificationTypes {
	private static final DeferredRegister<BiomeModificationType<?>> REGISTER = DeferredRegister.create(
		FrozenLibRegistries.BIOME_MODIFICATION_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<BiomeModificationType<?>, BiomeModificationType<NoOpModification>> NO_OP = register(
		"no_op",
		NoOpModification.codec(),
		false
	);
	public static final DeferredHolder<BiomeModificationType<?>, BiomeModificationType<EnvironmentAttributeModification>> ENVIRONMENT_ATTRIBUTE = register(
		"environment_attribute",
		EnvironmentAttributeModification.CODEC,
		false
	);
	public static final DeferredHolder<BiomeModificationType<?>, BiomeModificationType<SpecialEffectsModification>> SPECIAL_EFFECTS = register(
		"special_effects",
		SpecialEffectsModification.CODEC,
		false
	);
	public static final DeferredHolder<BiomeModificationType<?>, BiomeModificationType<FeatureModification>> FEATURE = register(
		"feature",
		FeatureModification.CODEC,
		true
	);
	public static final DeferredHolder<BiomeModificationType<?>, BiomeModificationType<CarverModification>> CARVER = register(
		"carver",
		CarverModification.CODEC,
		true
	);
	public static final DeferredHolder<BiomeModificationType<?>, BiomeModificationType<MobSpawnModification>> MOB_SPAWN = register(
		"mob_spawn",
		MobSpawnModification.CODEC,
		true
	);

	static {
		REGISTER.register();
	}

	public static void init() {
		// TODO: eventually fully data-drive modifications and tear down a ton of Fabric's impl
		for (ModificationPhase phase : ModificationPhase.values()) {
			BiomeModifications.create(FrozenLibConstants.id("dynamic_registry_modifications_at_" + phase.getSerializedName())).add(
				phase,
				BiomeSelectors.all(),
				(registryAccess, selectionContext, modificationContext) -> {
					registryAccess.lookupOrThrow(FrozenLibRegistries.BIOME_MODIFICATION).forEach(modification -> {
						if (!modification.phaseMatches(phase) || !modification.selectorMatches(selectionContext)) return;
						modification.modify(modificationContext);
					});
				}
			);
		}
	}

	private static <T extends BiomeModification> DeferredHolder<BiomeModificationType<?>, BiomeModificationType<T>> register(
		String name,
		MapCodec<T> codec,
		boolean serverOnly
	) {
		final BiomeModificationType<T> type = new BiomeModificationType<>(codec, serverOnly);
		return REGISTER.register(name, () -> type);
	}
}
