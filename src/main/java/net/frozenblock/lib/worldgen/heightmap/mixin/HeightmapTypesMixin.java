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

package net.frozenblock.lib.worldgen.heightmap.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import net.frozenblock.lib.worldgen.heightmap.api.FrozenHeightmaps;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Heightmap.Types.class)
public class HeightmapTypesMixin {

	//CREDIT TO nyuppo/fabric-boat-example ON GITHUB

	@SuppressWarnings("ShadowTarget")
	@Final
	@Shadow
	@Mutable
	private static Heightmap.Types[] $VALUES;

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static Heightmap.Types frozenLib$newType(String internalName, int internalId, String serializationKey, Heightmap.Usage usage, Predicate<BlockState> isOpaque) {
		throw new AssertionError("Mixin injection failed - FrozenLib HeightmapTypesMixin.");
	}

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.PUTSTATIC,
			target = "Lnet/minecraft/world/level/levelgen/Heightmap$Types;$VALUES:[Lnet/minecraft/world/level/levelgen/Heightmap$Types;",
			shift = At.Shift.AFTER
		)
	)
	private static void frozenLib$addHeightmaps(CallbackInfo info) {
		var types = new ArrayList<>(Arrays.asList($VALUES));
		var last = types.get(types.size() - 1);
		int lastOrdinal = last.ordinal();

		var motionBlockingOrFluidNoLeaves = frozenLib$newType(
			"FROZENLIBMOTION_BLOCKING_NO_LEAVES_SYNCED",
			lastOrdinal + 1,
			"FROZENLIB_MOTION_BLOCKING_OR_NO_LEAVES_SYNCED",
			Heightmap.Usage.CLIENT,
			state -> (state.blocksMotion() || !state.getFluidState().isEmpty()) && !(state.getBlock() instanceof LeavesBlock)
		);
		FrozenHeightmaps.MOTION_BLOCKING_NO_LEAVES_SYNCED = motionBlockingOrFluidNoLeaves;
		types.add(motionBlockingOrFluidNoLeaves);
		lastOrdinal += 1;

		$VALUES = types.toArray(new Heightmap.Types[0]);
	}

}
