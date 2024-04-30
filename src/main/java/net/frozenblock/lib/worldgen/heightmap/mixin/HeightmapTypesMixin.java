/*
 * Copyright (C) 2024 FrozenBlock
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
