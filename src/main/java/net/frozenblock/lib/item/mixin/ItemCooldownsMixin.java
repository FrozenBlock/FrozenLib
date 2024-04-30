/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.item.mixin;

import java.util.Map;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemCooldowns.class)
public class ItemCooldownsMixin implements CooldownInterface {

    @Final
    @Shadow
    public Map<Item, ItemCooldowns.CooldownInstance> cooldowns;

	@Unique
	@Override
    public void frozenLib$changeCooldown(Item item, int additional) {
		this.cooldowns.computeIfPresent(item, (item1, cooldown) -> {
            this.frozenLib$onCooldownChanged(item, additional);
			return new ItemCooldowns.CooldownInstance(cooldown.startTime, cooldown.endTime + additional);
        });
    }

	@Unique
	@Override
    public void frozenLib$onCooldownChanged(Item item, int additional) {
    }

}
