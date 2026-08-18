package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

@UtilityClass
public final class ConventionalEnchantmentTags {
	public static final TagKey<Enchantment> INCREASE_BLOCK_DROPS = bind("increase_block_drops");
	public static final TagKey<Enchantment> INCREASE_ENTITY_DROPS = bind("increase_entity_drops");
	public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENTS = bind("weapon_damage_enhancements");
	public static final TagKey<Enchantment> ENTITY_SPEED_ENHANCEMENTS = bind("entity_speed_enhancements");
	public static final TagKey<Enchantment> ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS = bind("entity_auxiliary_movement_enhancements");
	public static final TagKey<Enchantment> ENTITY_DEFENSE_ENHANCEMENTS = bind("entity_defense_enhancements");
	public static final TagKey<Enchantment> HIDDEN_FROM_RECIPE_VIEWERS = bind("hidden_from_recipe_viewers");

	private static TagKey<Enchantment> bind(String path) {
		return TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("c", path));
	}
}
