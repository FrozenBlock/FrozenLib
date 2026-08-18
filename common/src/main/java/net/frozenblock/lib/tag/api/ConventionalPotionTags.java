package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.alchemy.Potion;

@UtilityClass
public final class ConventionalPotionTags {
	public static final TagKey<Potion> HIDDEN_FROM_RECIPE_VIEWERS = bind("hidden_from_recipe_viewers");

	private static TagKey<Potion> bind(String path) {
		return TagKey.create(Registries.POTION, Identifier.fromNamespaceAndPath("c", path));
	}
}
