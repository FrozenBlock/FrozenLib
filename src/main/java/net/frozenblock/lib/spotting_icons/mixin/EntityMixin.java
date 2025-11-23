/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.spotting_icons.mixin;

import net.frozenblock.lib.spotting_icons.api.SpottingIconManager;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements EntitySpottingIconInterface {

	@Unique
	public SpottingIconManager frozenLib$SpottingIconManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void frozenLib$setIconManager(EntityType<?> entityType, Level level, CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
		this.frozenLib$SpottingIconManager = new SpottingIconManager(entity);
    }

    @Inject(
		method = "saveWithoutId",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V",
			shift = At.Shift.AFTER
		)
	)
    public void frozenLib$saveIconManager(ValueOutput output, CallbackInfo info) {
		if (this.frozenLib$SpottingIconManager != null) this.frozenLib$SpottingIconManager.save(output);
    }

    @Inject(
		method = "load",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V",
			shift = At.Shift.AFTER
		)
	)
    public void frozenLib$loadIconManager(ValueInput input, CallbackInfo info) {
		this.frozenLib$SpottingIconManager.load(input);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void frozenLib$tickIcon(CallbackInfo info) {
		final Entity entity = Entity.class.cast(this);
        if (!entity.level().isClientSide()) this.frozenLib$SpottingIconManager.tick();
    }

	@Unique
	@Override
	public SpottingIconManager getSpottingIconManager() {
		return this.frozenLib$SpottingIconManager;
	}

}
