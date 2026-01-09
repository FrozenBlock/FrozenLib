/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.beacon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BeaconBlockEntity.class, priority = 10000)
public class BeaconBlockEntityMixin {

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
			ordinal = 0
		)
	)
	private static List<Holder<MobEffect>> wilderWild$addTier1Effects(List<Holder<MobEffect>> list) {
		return new ArrayList<>(list);
	}

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
			ordinal = 1
		)
	)
	private static List<Holder<MobEffect>> wilderWild$addTier2Effects(List<Holder<MobEffect>> list) {
		return new ArrayList<>(list);
	}

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;of(Ljava/lang/Object;)Ljava/util/List;",
			ordinal = 0
		)
	)
	private static List<Holder<MobEffect>> wilderWild$addTier3Effects(List<Holder<MobEffect>> list) {
		return new ArrayList<>(list);
	}

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;of(Ljava/lang/Object;)Ljava/util/List;",
			ordinal = 1
		)
	)
	private static List<Holder<MobEffect>> wilderWild$addTier4Effects(List<Holder<MobEffect>> list) {
		return new ArrayList<>(list);
	}

}
