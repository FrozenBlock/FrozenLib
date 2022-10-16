package net.frozenblock.lib.mixin.server;

import com.google.common.collect.Maps;
import net.frozenblock.lib.interfaces.CooldownInterface;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(ItemCooldowns.class)
public class ItemCooldownsMixin implements CooldownInterface {

    @Final
    @Shadow
    @Mutable
    public final Map<Item, ItemCooldowns.CooldownInstance> cooldowns = Maps.newHashMap();

	@Unique
	@Override
    public void changeCooldown(Item item, int additional) {
        if (this.cooldowns.containsKey(item)) {
            ItemCooldowns.CooldownInstance cooldown = this.cooldowns.get(item);
            this.cooldowns.put(item, new ItemCooldowns.CooldownInstance(cooldown.startTime, cooldown.endTime + additional));
            this.onCooldownChanged(item, additional);
        }
    }

	@Unique
	@Override
    public void onCooldownChanged(Item item, int additional) {
    }

}
