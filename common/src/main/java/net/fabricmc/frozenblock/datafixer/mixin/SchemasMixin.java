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

package net.fabricmc.frozenblock.datafixer.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import net.fabricmc.frozenblock.datafixer.impl.FabricSubSchema;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.SharedConstants;

@Mixin(DataFixers.class)
public class SchemasMixin {

	// TODO: rework this so it can actually apply to Vanilla schemas. This implementation effectively accomplishes absolutely nothing.
	// TODO: but embarrass us.
	@Inject(method = "addFixers", at = @At("TAIL"))
	private static void frozenLib$addEntrypointSchema(DataFixerBuilder fixerUpper, FileFixerUpper.Builder fileFixerUpper, CallbackInfo info) {
		final int version = SharedConstants.getCurrentVersion().dataVersion().version() - 1;
		final FabricSubSchema schema = (FabricSubSchema) fixerUpper.addSchema(version, FabricSubSchema::new);

		if (!schema.registeredBlockEntities.getKeys().isEmpty()) {
			fixerUpper.addFixer(new AddNewChoices(schema, "Add FrozenLib-registered block entities.", References.BLOCK_ENTITY));
		}

		if (!schema.registeredEntities.getKeys().isEmpty()) {
			fixerUpper.addFixer(new AddNewChoices(schema, "Add FrozenLib-registered entities.", References.ENTITY));
		}

		schema.registeredBlockEntities = null;
		schema.registeredEntities = null;
	}
}
