/*
 * Copyright 2023 FrozenBlock
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
import net.minecraft.world.entity.LivingEntity;

public class FrozenDamageSource extends DamageSource {

    public FrozenDamageSource(String name) {
        super(name);
    }

    public static DamageSource source(String name) {
        return new FrozenDamageSource(name);
    }

    public Component getLocalizedDeathMessage(LivingEntity entity) {
        LivingEntity livingEntity = entity.getKillCredit();
        String string = "death.attack." + this.msgId;
        String string2 = string + ".player";
        if (livingEntity != null) {
            return Component.translatable(string2, entity.getDisplayName(), livingEntity.getDisplayName());
        }
        return Component.translatable(string, entity.getDisplayName());
    }

}
