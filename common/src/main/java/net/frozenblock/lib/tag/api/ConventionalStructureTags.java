package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

@UtilityClass
public final class ConventionalStructureTags {
	public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = bind("hidden_from_displayers");
	public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = bind("hidden_from_locator_selection");

	private static TagKey<Structure> bind(String path) {
		return TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("c", path));
	}
}
