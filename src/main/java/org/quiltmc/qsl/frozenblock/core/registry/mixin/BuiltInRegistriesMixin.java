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
 */

package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import java.util.List;
import net.fabricmc.fabric.mixin.registry.sync.DebugChunkGeneratorAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

	@Inject(method = "freeze", at = @At("RETURN"))
	private static void onFreezeBuiltins(CallbackInfo ci) {
		//region Fix MC-197259
		final List<BlockState> states = BuiltInRegistries.BLOCK.stream()
				.flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
				.toList();

		final int xLength = Mth.ceil(Mth.sqrt(states.size()));
		final int zLength = Mth.ceil(states.size() / (float) xLength);

		DebugChunkGeneratorAccessor.setBLOCK_STATES(states);
		DebugChunkGeneratorAccessor.setX_SIDE_LENGTH(xLength);
		DebugChunkGeneratorAccessor.setZ_SIDE_LENGTH(zLength);
		//endregion
	}
}
