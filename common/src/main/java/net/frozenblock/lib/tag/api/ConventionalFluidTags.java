package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

@UtilityClass
public final class ConventionalFluidTags {
	public static final TagKey<Fluid> LAVA = bind("lava");
	public static final TagKey<Fluid> WATER = bind("water");
	public static final TagKey<Fluid> MILK = bind("milk");
	public static final TagKey<Fluid> HONEY = bind("honey");
	public static final TagKey<Fluid> GASEOUS = bind("gaseous");
	public static final TagKey<Fluid> EXPERIENCE = bind("experience");
	public static final TagKey<Fluid> POTION = bind("potion");
	public static final TagKey<Fluid> SUSPICIOUS_STEW = bind("suspicious_stew");
	public static final TagKey<Fluid> MUSHROOM_STEW = bind("mushroom_stew");
	public static final TagKey<Fluid> RABBIT_STEW = bind("rabbit_stew");
	public static final TagKey<Fluid> BEETROOT_SOUP = bind("beetroot_soup");
	public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = bind("hidden_from_recipe_viewers");

	private static TagKey<Fluid> bind(String path) {
		return TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath("c", path));
	}
}
