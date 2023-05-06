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

package net.frozenblock.lib.item.mixin;

import java.util.List;
import java.util.UUID;

import net.frozenblock.lib.item.api.HeavyItemDamageRegistry;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

	@Shadow @Nullable
	private UUID thrower;

	@Unique
	private boolean frozenLib$isHeavy;

	@Inject(method = "setItem", at = @At("TAIL"))
	public void setItem(ItemStack stack, CallbackInfo info) {
		this.frozenLib$isHeavy = stack.is(FrozenItemTags.HEAVY_ITEMS);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", shift = At.Shift.BEFORE))
	public void hitIfHeavy(CallbackInfo info) {
		ItemEntity item = ItemEntity.class.cast(this);
		if (this.frozenLib$isHeavy) {
			List<Entity> entities = this.collidingEntities();
			for (Entity entity : entities) {
				if (!item.isRemoved() && entity != null) {
					boolean shouldDamage = true;
					if (entity instanceof Player player) {
						if (player.isCreative()) {
							shouldDamage = false;
						}
					}
					if (entity.isInvulnerable()) {
						shouldDamage = false;
					}
					if (shouldDamage) {
						this.hitEntity(entity);
					}
				}
			}
		}
	}

	@Unique
	private void hitEntity(Entity entity) {
		ItemEntity item = ItemEntity.class.cast(this);
		Entity owner = this.thrower != null ? item.level().getPlayerByUUID(this.thrower) : null;
		if (entity != owner) {
			DamageSource damageSource = owner.damageSources().mobAttack((LivingEntity) entity);
			if (owner != null) {
				((LivingEntity) owner).setLastHurtMob(entity);
			}
			if (entity.hurt(damageSource, HeavyItemDamageRegistry.getDamage(this.getItem()))) {
				//TODO: Bonk sound
				item.playSound(SoundEvents.ANVIL_LAND, 0.3F, 1.2F / (item.level().random.nextFloat() * 0.2F + 0.9F));
			}
		}
	}

	@Unique
	public List<Entity> collidingEntities() {
		ItemEntity entity = ItemEntity.class.cast(this);
		return entity.level().getEntities(entity, entity.getBoundingBox().expandTowards(entity.getDeltaMovement()), this::canHitEntity);
	}

	@Unique
	public boolean canHitEntity(Entity entity) {
		ItemEntity item = ItemEntity.class.cast(this);
		Vec3 itemMovement = item.getDeltaMovement();
		if (!entity.isSpectator() && entity.isAlive() && entity.isPickable() && entity instanceof LivingEntity && itemMovement.length() > 0.3) {
			Vec3 compared = entity.getDeltaMovement().subtract(itemMovement);
			return compared.horizontalDistance() > 0.4 || compared.y > 0.3;
		} else {
			return false;
		}
	}

	@Shadow
	public ItemStack getItem() {
		throw new AssertionError("Mixin injection failed - FrozenLib ItemEntityMixin");
	}

}
