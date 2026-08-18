package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

@UtilityClass
public final class ConventionalEntityTypeTags {
	public static final TagKey<EntityType<?>> BOSSES = bind("bosses");
	public static final TagKey<EntityType<?>> MINECARTS = bind("minecarts");
	public static final TagKey<EntityType<?>> BOATS = bind("boats");
	public static final TagKey<EntityType<?>> ITEM_FRAMES = bind("item_frames");
	public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = bind("capturing_not_supported");
	public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = bind("teleporting_not_supported");

	private static TagKey<EntityType<?>> bind(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", path));
	}
}
