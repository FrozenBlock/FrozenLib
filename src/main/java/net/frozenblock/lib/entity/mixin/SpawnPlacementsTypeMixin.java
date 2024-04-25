/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
