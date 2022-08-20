package net.frozenblock.lib.item;

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

    public PrickOnUseItem(Item.Properties properties, float damage, @Nullable SoundEvent sound) {
        super(properties);
        this.damage = damage;
        this.hurtSound = sound;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (this.isEdible()) {
            user.hurt(FrozenDamageSource.source("prick"),2.0F);
            if (this.hurtSound != null && !user.isSilent()) {
                user.playSound(this.hurtSound, 0.4F, 0.9F + (world.random.nextFloat() * 0.2F));
            }
            return user.eat(world, stack);
        }
        return stack;
    }

}
