/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.items;

import net.frozenblock.lib.damagesource.FrozenDamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PrickOnUseItem extends Item {
    public final float damage;
    public final SoundEvent hurtSound;
    public final String damageSourceName;

    public PrickOnUseItem(Item.Properties properties, float damage, @Nullable SoundEvent sound, String damageSourceName) {
        super(properties);
        this.damage = damage;
        this.hurtSound = sound;
        this.damageSourceName = damageSourceName;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (this.isEdible()) {
            user.hurt(FrozenDamageSource.source(damageSourceName),this.damage);
            if (this.hurtSound != null && !user.isSilent()) {
                user.playSound(this.hurtSound, 0.5F, 0.9F + (world.random.nextFloat() * 0.2F));
            }
            return user.eat(world, stack);
        }
        return stack;
    }

}
