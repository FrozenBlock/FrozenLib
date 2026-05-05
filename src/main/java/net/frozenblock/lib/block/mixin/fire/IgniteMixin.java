package net.frozenblock.lib.block.mixin.fire;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.Ignite;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ignite.class)
public class IgniteMixin {

	@Inject(
		method = "apply",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
		)
	)
	public void frozenLib$setFireType(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 position, CallbackInfo info) {
		final LivingEntity owner = item.owner();
		if (owner == null) return;

		final FireData fireData = owner.getAttached(FireData.ATTACHMENT);
		if (fireData == null || !fireData.type().value().spreadsFromIgniteEnchantments()) return;

		FireData.trySet(entity, fireData.type());
	}
}
