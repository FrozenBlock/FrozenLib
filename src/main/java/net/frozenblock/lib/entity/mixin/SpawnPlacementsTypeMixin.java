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

package net.frozenblock.lib.entity.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import net.frozenblock.lib.entity.api.FrozenSpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnPlacements.Type.class)
public class SpawnPlacementsTypeMixin {

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static SpawnPlacements.Type frozenLib$newType(String internalName, int internalId) {
		throw new AssertionError("Mixin injection failed - FrozenLib SpawnPlacementsTypeMixin.");
	}

	@SuppressWarnings("ShadowTarget")
	@Shadow
	@Final
	@Mutable
	private static SpawnPlacements.Type[] $VALUES;

	@Inject(
			method = "<clinit>",
			at = @At(
					value = "FIELD",
					opcode = Opcodes.PUTSTATIC,
					target = "Lnet/minecraft/world/entity/SpawnPlacements$Type;$VALUES:[Lnet/minecraft/world/entity/SpawnPlacements$Type;",
					shift = At.Shift.AFTER
			)
	)
	private static void addCustomCategories(CallbackInfo info) {
		var types = new ArrayList<>(Arrays.asList($VALUES));
		var last = types.get(types.size() - 1);
		int currentOrdinal = last.ordinal();

		var lavaSurface = frozenLib$newType("FROZENLIB$LAVA_SURFACE", currentOrdinal + 1);
		FrozenSpawnPlacementTypes.ON_GROUND_OR_ON_LAVA_SURFACE = lavaSurface;
		types.add(lavaSurface);
		currentOrdinal += 1;

		$VALUES = types.toArray(new SpawnPlacements.Type[0]);
	}
}
