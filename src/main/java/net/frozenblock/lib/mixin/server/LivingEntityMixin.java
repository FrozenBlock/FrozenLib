package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.interfaces.EntityLoopingSoundInterface;
import net.frozenblock.lib.sound.FrozenClientPacketInbetween;
import net.frozenblock.lib.sound.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.tags.FrozenItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EntityLoopingSoundInterface {

    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;

    public MovingLoopingSoundEntityManager loopingSoundManager;
    public boolean clientFrozenSoundSync;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        this.loopingSoundManager = new MovingLoopingSoundEntityManager(entity);
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void startUsingItem(InteractionHand hand, CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        ItemStack stack = entity.getItemInHand(hand);
        if (!stack.isEmpty() && !entity.isUsingItem()) {
            if (stack.is(FrozenItemTags.NO_USE_GAME_EVENTS)) {
                info.cancel();
                this.useItem = stack;
                this.useItemRemaining = stack.getUseDuration();
                if (!entity.level.isClientSide) {
                    this.setLivingEntityFlag(1, true);
                    this.setLivingEntityFlag(2, hand == InteractionHand.OFF_HAND);
                }
            }
        }
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"), cancellable = true)
    public void stopUsingItem(CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!entity.level.isClientSide) {
            ItemStack stack = entity.getUseItem();
            if (stack.is(FrozenItemTags.NO_USE_GAME_EVENTS)) {
                this.setLivingEntityFlag(1, false);
                info.cancel();
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo info) {
        if (this.loopingSoundManager != null) {
            this.loopingSoundManager.save(compoundTag);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo info) {
        this.loopingSoundManager.load(compoundTag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!entity.level.isClientSide) {
            this.loopingSoundManager.tick();
        } else if (!this.clientFrozenSoundSync) {
            FrozenClientPacketInbetween.requestFrozenSoundSync(entity.getId(), entity.level.dimension());
            this.clientFrozenSoundSync = true;
        }
    }

    @Shadow
    protected void setLivingEntityFlag(int mask, boolean value) {

    }

    @Override
    public boolean hasSyncedClient() {
        return this.clientFrozenSoundSync;
    }

    @Override
    public MovingLoopingSoundEntityManager getSounds() {
        return this.loopingSoundManager;
    }

    @Override
    public void addSound(ResourceLocation soundID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId) {
        this.loopingSoundManager.addSound(soundID, category, volume, pitch, restrictionId);
    }
}
