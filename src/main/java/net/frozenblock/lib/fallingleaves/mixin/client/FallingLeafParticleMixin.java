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

package net.frozenblock.lib.fallingleaves.mixin.client;

import net.frozenblock.lib.wind.api.ClientWindManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.particle.FallingLeafParticle;

@Mixin(FallingLeafParticle.class)
public abstract class FallingLeafParticleMixin extends TextureSheetParticle {

	@Unique
	private double frozenLib$savedXMovement;

	@Unique
	private double frozenLib$savedZMovement;

	@Shadow @Final
	public float windCoefficient;

	protected FallingLeafParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
		super(clientLevel, d, e, f);
	}

	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lrandommcsomethin/fallingleaves/particle/FallingLeafParticle;xd:D", ordinal = 1, shift = At.Shift.BEFORE))
	public void frozenLib$saveLeafMovement(CallbackInfo info) {
		this.frozenLib$savedXMovement = this.xd;
		this.frozenLib$savedZMovement = this.zd;
	}

	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lrandommcsomethin/fallingleaves/particle/FallingLeafParticle;zd:D", ordinal = 2, shift = At.Shift.AFTER))
	public void frozenLib$newLeafMovement(CallbackInfo info) {
		Vec3 frozenLib$wind = ClientWindManager.getWindMovement(this.level, BlockPos.containing(this.x, this.y, this.z));
		this.xd = (frozenLib$wind.x() - this.frozenLib$savedXMovement) * (double)this.windCoefficient / 60.0;
		this.zd = (frozenLib$wind.z() - this.frozenLib$savedZMovement) * (double)this.windCoefficient / 60.0;
	}

}
