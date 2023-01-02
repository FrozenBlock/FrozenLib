/*
 * Copyright 2022-2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.damagesource.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FrozenProjectileDamageSource extends IndirectEntityDamageSource {
    public FrozenProjectileDamageSource(String name, Entity projectile, @Nullable Entity attacker) {
        super(name, projectile, attacker);
    }

    public static DamageSource source(String name, Entity projectile, @Nullable Entity attacker) {
        return new FrozenProjectileDamageSource(name, projectile, attacker);
    }

    public Component getLocalizedDeathMessage(LivingEntity entity) {
        Component text = this.getEntity() == null ? this.entity.getDisplayName() : this.getEntity().getDisplayName();
        ItemStack itemStack = this.getEntity() instanceof LivingEntity ? ((LivingEntity) this.getEntity()).getMainHandItem() : ItemStack.EMPTY;
        String string = "death.attack." + this.msgId;
        String string2 = string + ".item";
        return !itemStack.isEmpty() && itemStack.hasCustomHoverName() ? Component.translatable(string2, entity.getDisplayName(), text, itemStack.getDisplayName()) : Component.translatable(string, entity.getDisplayName(), text);
    }
}
