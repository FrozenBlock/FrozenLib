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

package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class FrozenLibBiomeSelectors {
	public static final ResourceKey<BiomeSelector> IN_OVERWORLD = createKey("in_overworld");
	public static final ResourceKey<BiomeSelector> IN_NETHER = createKey("in_nether");
	public static final ResourceKey<BiomeSelector> IN_SIFT = createKey("in_sift");
	public static final ResourceKey<BiomeSelector> IN_END = createKey("in_end");

	@ApiStatus.Internal
	public static void bootstrap(BootstrapContext<BiomeSelector> context) {
		context.register(
			IN_OVERWORLD,
			BiomeSelector.anyOf(
				BiomeSelector.inDimension(LevelStem.OVERWORLD),
				BiomeSelector.inBiomeTag(context, BiomeTags.IS_OVERWORLD)
			)
		);

		context.register(
			IN_NETHER,
			BiomeSelector.anyOf(
				BiomeSelector.inDimension(LevelStem.NETHER),
				BiomeSelector.inBiomeTag(context, BiomeTags.IS_NETHER)
			)
		);

		//TODO: sift moment
		context.register(
			IN_SIFT,
			BiomeSelector.inDimension(ResourceKey.create(Registries.LEVEL_STEM, Identifier.withDefaultNamespace("the_sift")))
		);

		/*
		context.register(
			IN_SIFT,
			BiomeSelector.anyOf(
				BiomeSelector.inDimension(LevelStem.SIFT),
				BiomeSelector.inBiomeTag(context, BiomeTags.IS_SIFT)
			)
		);
		 */

		context.register(
			IN_END,
			BiomeSelector.anyOf(
				BiomeSelector.inDimension(LevelStem.END),
				BiomeSelector.inBiomeTag(context, BiomeTags.IS_END)
			)
		);
	}

	private static ResourceKey<BiomeSelector> createKey(String name) {
		return ResourceKey.create(FrozenLibRegistries.BIOME_SELECTOR_PROVIDER, FrozenLibConstants.id(name));
	}
}
