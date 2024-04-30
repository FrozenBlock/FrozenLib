/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
import org.jetbrains.annotations.NotNull;
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
	public boolean canHitEntity(@NotNull Entity entity) {
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
