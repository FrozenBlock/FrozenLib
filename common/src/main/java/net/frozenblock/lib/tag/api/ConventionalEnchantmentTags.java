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
