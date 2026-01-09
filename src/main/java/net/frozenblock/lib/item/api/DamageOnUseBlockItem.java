/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.item.api;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class DamageOnUseBlockItem extends BlockItem {
    public final float damage;
    public final SoundEvent hurtSound;
    public final ResourceKey<DamageType> damageType;

    public DamageOnUseBlockItem(Block block, Properties properties, float damage, @Nullable SoundEvent sound, ResourceKey<DamageType> damageType) {
        super(block, properties);
        this.damage = damage;
        this.hurtSound = sound;
        this.damageType = damageType;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		final Consumable consumable = stack.get(DataComponents.CONSUMABLE);
		if (consumable == null) return stack;

		entity.hurt(entity.damageSources().source(this.damageType),this.damage);
		if (this.hurtSound != null && !entity.isSilent()) {
			entity.playSound(this.hurtSound, 0.5F, 0.9F + (level.getRandom().nextFloat() * 0.2F));
		}
		return consumable.onConsume(level, entity, stack);
    }

}
