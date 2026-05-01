/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.DataFixers;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

/**
 * New mixin by FrozenBlock.
 */
@Mixin(value = DataFixerUpper.class, priority = 1001)
public class DataFixerUpperMixin {

	@ModifyReturnValue(method = "update", at = @At("RETURN"))
	public <T> Dynamic<T> frozenLib$updateWithDataFixers(Dynamic<T> original, DSL.TypeReference type) {
		if (DataFixerUpper.class.cast(this) != DataFixers.getDataFixer()) return original;
		return QuiltDataFixesInternals.get().updateWithAllFixers(type, original, Optional.empty());
	}
}
