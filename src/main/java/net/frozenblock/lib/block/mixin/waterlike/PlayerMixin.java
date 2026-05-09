/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.waterlike;

import it.unimi.dsi.fastutil.objects.Reference2BooleanArrayMap;
import java.util.Map;
import java.util.Optional;
import net.frozenblock.lib.block.api.waterlike.WaterLikeTypes;
import net.frozenblock.lib.block.impl.waterlike.PlayerInWaterLikeInterface;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements PlayerInWaterLikeInterface {

	@Unique
	private final Map<WaterLikeType, Boolean> frozenLib$playerInsideWaterLikeStatuses = new Reference2BooleanArrayMap<>();

	@Inject(method = "updateIsUnderwater", at = @At(value = "HEAD"))
	public void frozenLib$updateIsInWaterLike(CallbackInfoReturnable<Boolean> info) {
		this.frozenLib$playerInsideWaterLikeStatuses.clear();
		this.frozenLib$playerInsideWaterLikeStatuses.putAll(Player.class.cast(this).frozenLib$inWaterLikeStatuses());
	}

	@Unique
	@Override
	public void frozenLib$setPlayerInWaterLike(WaterLikeType type, boolean inside) {
		this.frozenLib$playerInsideWaterLikeStatuses.put(type, inside);
	}

	@Unique
	@Override
	public boolean frozenLib$wasPlayerInWaterLike(WaterLikeType type) {
		return this.frozenLib$playerInsideWaterLikeStatuses.getOrDefault(type, false);
	}

	@Unique
	@Override
	public Map<WaterLikeType, Boolean> frozenLib$playerInWaterLikeStatuses() {
		return this.frozenLib$playerInsideWaterLikeStatuses;
	}

	@Inject(method = "getSwimSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouching(Player.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().playerSwimSound().value());
	}

	@Inject(method = "getSwimSplashSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimSplashSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouching(Player.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().playerSplashSound().value());
	}

	@Inject(method = "getSwimHighSpeedSplashSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimHighSpeedSplashSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouching(Player.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().playerSplashHighSpeedSound().value());
	}
}
