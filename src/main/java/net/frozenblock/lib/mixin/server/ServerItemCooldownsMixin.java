package net.frozenblock.lib.mixin.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.interfaces.CooldownInterface;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ServerItemCooldowns;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerItemCooldowns.class)
public class ServerItemCooldownsMixin extends ItemCooldowns implements CooldownInterface {
    @Shadow
	@Final
    private ServerPlayer player;

	@Unique
	@Override
    public void changeCooldown(Item item, int additional) {
        if (this.cooldowns.containsKey(item)) {
            CooldownInstance cooldown = this.cooldowns.get(item);
            this.cooldowns.put(item, new CooldownInstance(cooldown.startTime, cooldown.endTime + additional));
            this.onCooldownChanged(item, additional);
        }
    }

	@Unique
	@Override
    public void onCooldownChanged(Item item, int additional) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeId(Registry.ITEM, item);
        byteBuf.writeVarInt(additional);
        ServerPlayNetworking.send(this.player, FrozenMain.COOLDOWN_CHANGE_PACKET, byteBuf);
    }

}
