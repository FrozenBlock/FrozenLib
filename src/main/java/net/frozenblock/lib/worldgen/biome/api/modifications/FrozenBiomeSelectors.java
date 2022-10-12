package net.frozenblock.lib.worldgen.biome.api.modifications;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Predicate;

/**
 * Provides several biome selectors with additional functionality.
 *
 * <p>Based on {@link BiomeSelectors}
 */
public final class FrozenBiomeSelectors {

    private FrozenBiomeSelectors() {
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the Overworld,
     * assuming Vanilla's default biome source is used.
     */
    public static Predicate<BiomeSelectionContext> foundInOverworld() {
        return context -> context.hasTag(BiomeTags.IS_OVERWORLD) || context.canGenerateIn(LevelStem.OVERWORLD);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the Nether,
     * assuming Vanilla's default multi noise biome source with the nether preset is used.
     *
     * <p>This selector will also match modded biomes that have been added to the nether using {@link NetherBiomes}.
     */
    public static Predicate<BiomeSelectionContext> foundInTheNether() {
        return context -> context.hasTag(BiomeTags.IS_NETHER) || context.canGenerateIn(LevelStem.NETHER);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the End,
     * assuming Vanilla's default End biome source is used.
     */
    public static Predicate<BiomeSelectionContext> foundInTheEnd() {
        return context -> context.hasTag(BiomeTags.IS_END) || context.canGenerateIn(LevelStem.END);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the Overworld,
     * assuming Vanilla's default biome source is used, except for biomes in the specified tag.
     */
    public static Predicate<BiomeSelectionContext> foundInOverworldExcept(TagKey<Biome> except) {
        return context -> (context.hasTag(BiomeTags.IS_OVERWORLD) || context.canGenerateIn(LevelStem.OVERWORLD)) && !context.hasTag(except);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the Nether,
     * assuming Vanilla's default multi noise biome source with the nether preset is used, except for biomes in the specified tag.
     *
     * <p>This selector will also match modded biomes that have been added to the nether using {@link NetherBiomes}.
     */
    public static Predicate<BiomeSelectionContext> foundInTheNetherExcept(TagKey<Biome> except) {
        return context -> (context.hasTag(BiomeTags.IS_NETHER) || context.canGenerateIn(LevelStem.NETHER)) && !context.hasTag(except);
    }

    /**
     * Returns a biome selector that will match all biomes that would normally spawn in the End,
     * assuming Vanilla's default End biome source is used, except for biomes in the specified tag.
     */
    public static Predicate<BiomeSelectionContext> foundInTheEndExcept(TagKey<Biome> except) {
        return context -> (context.hasTag(BiomeTags.IS_END) || context.canGenerateIn(LevelStem.END)) && !context.hasTag(except);
    }
}
