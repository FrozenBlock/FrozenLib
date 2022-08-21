package net.frozenblock.lib.item;

import net.frozenblock.lib.interfaces.CooldownInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

public class CooldownChange {

    public static void changeCooldown(Player player, Item item, int additionalCooldown, int min) {
        ItemCooldowns manager = player.getCooldowns();
        ItemCooldowns.CooldownInstance entry = manager.cooldowns.get(item);
        if (entry != null) {
            int between = entry.endTime - entry.startTime;
            if ((between + additionalCooldown) > min) {
                ((CooldownInterface)player.getCooldowns()).changeCooldown(item, additionalCooldown);
            }
        }
    }

}
