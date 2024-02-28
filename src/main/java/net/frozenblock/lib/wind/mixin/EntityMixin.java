/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.wind.mixin;

import net.frozenblock.lib.wind.impl.InWorldWindModifier;
import net.frozenblock.lib.wind.api.WindDisturbingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements WindDisturbingEntity {

	@Unique
	private InWorldWindModifier frozenLib$inWorldWindModifier = InWorldWindModifier.DUMMY_IN_WORLD_WIND_MODIFIER;


	@Inject(
		method = "baseTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;checkBelowWorld()V"
		)
	)
	public void frozenLib$baseTick(CallbackInfo info) {
		this.frozenLib$makeAndSetInWorldWindModifier();
	}

	@Unique
	@Override
	public InWorldWindModifier frozenLib$getInWorldWindModifier() {
		return this.frozenLib$inWorldWindModifier;
	}

	@Unique
	@Nullable
	@Override
	public InWorldWindModifier.Modifier frozenLib$makeWindModifier() {
		return null;
	}

	@Unique
	@Override
	public double frozenLib$getWindWidth() {
		return 2D;
	}

	@Unique
	@Override
	public double frozenLib$getWindHeight() {
		return 2D;
	}

	@Unique
	private void frozenLib$makeAndSetInWorldWindModifier() {
		Entity entity = Entity.class.cast(this);
		InWorldWindModifier.Modifier modifier = this.frozenLib$makeWindModifier();
		if (modifier == null) {
			this.frozenLib$inWorldWindModifier = InWorldWindModifier.DUMMY_IN_WORLD_WIND_MODIFIER;
		} else {
			this.frozenLib$inWorldWindModifier = new InWorldWindModifier(
				entity.position(),
				AABB.ofSize(entity.position(), this.frozenLib$getWindWidth(), this.frozenLib$getWindHeight(), this.frozenLib$getWindWidth()),
				this.frozenLib$makeWindModifier()
			);
		}
	}

}
