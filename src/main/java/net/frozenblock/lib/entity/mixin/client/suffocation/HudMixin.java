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

package net.frozenblock.lib.entity.mixin.client.suffocation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.entity.client.impl.suffocation.SuffocationBubbleRenderer;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(Hud.class)
public class HudMixin {

	@ModifyExpressionValue(
		method = "extractAirBubbles",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")
	)
	private boolean frozenLib$suffocationActsLikeUnderwater(boolean original, @Local(argsOnly = true) Player player) {
		return original || SuffocationBubbleRenderer.hasHazard(player);
	}

	@ModifyVariable(method = "extractAirBubbles", at = @At("STORE"), name = "fullAirBubbles")
	private int frozenLib$airCount(int fullAirBubbles, @Local(argsOnly = true) Player player) {
		if (frozenLib$gasOnly(player)) return 0;
		return SuffocationBubbleRenderer.hasHazard(player) ? Math.min(fullAirBubbles, SuffocationBubbleRenderer.freeAirBubbles(player)) : fullAirBubbles;
	}

	@ModifyVariable(method = "extractAirBubbles", at = @At("STORE"), name = "emptyAirBubbles")
	private int frozenLib$lostRegion(int emptyAirBubbles, @Local(argsOnly = true) Player player, @Local(name = "fullAirBubbles") int fullAirBubbles) {
		if (frozenLib$gasOnly(player)) return SuffocationBubbleRenderer.gasCount(player);
		return SuffocationBubbleRenderer.hasHazard(player) ? 10 - fullAirBubbles : emptyAirBubbles;
	}

	@ModifyVariable(method = "extractAirBubbles", at = @At("STORE"), name = "poppingAirBubblePosition")
	private int frozenLib$waterPopPos(int poppingAirBubblePosition, @Local(argsOnly = true) Player player) {
		return frozenLib$gasOnly(player) ? 0 : poppingAirBubblePosition;
	}

	@ModifyExpressionValue(
		method = "extractAirBubbles",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Hud;AIR_SPRITE:Lnet/minecraft/resources/Identifier;", opcode = Opcodes.GETSTATIC)
	)
	private Identifier frozenLib$airSprite(Identifier original, @Local(argsOnly = true) Player player, @Local(name = "airBubble") int airBubble) {
		return SuffocationBubbleRenderer.airSprite(player, airBubble, original);
	}

	@ModifyExpressionValue(
		method = "extractAirBubbles",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Hud;AIR_POPPING_SPRITE:Lnet/minecraft/resources/Identifier;", opcode = Opcodes.GETSTATIC)
	)
	private Identifier frozenLib$poppingSprite(Identifier original, @Local(argsOnly = true) Player player, @Local(name = "airBubble") int airBubble) {
		return SuffocationBubbleRenderer.poppingSprite(player, airBubble, original);
	}

	@ModifyExpressionValue(
		method = "extractAirBubbles",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Hud;AIR_EMPTY_SPRITE:Lnet/minecraft/resources/Identifier;", opcode = Opcodes.GETSTATIC)
	)
	private Identifier frozenLib$emptySprite(Identifier original, @Local(argsOnly = true) Player player, @Local(name = "airBubble") int airBubble) {
		return SuffocationBubbleRenderer.emptySprite(player, airBubble, original);
	}

	private static boolean frozenLib$gasOnly(Player player) {
		return SuffocationBubbleRenderer.hasHazard(player)
			&& !SuffocationBubbleRenderer.underwater(player)
			&& SuffocationBubbleRenderer.waterAirBubbles(player) >= SuffocationBubbleRenderer.freeAirBubbles(player);
	}
}
