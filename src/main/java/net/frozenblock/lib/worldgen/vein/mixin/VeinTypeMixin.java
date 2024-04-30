/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.vein.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.worldgen.vein.api.FrozenVeinTypes;
import net.frozenblock.lib.worldgen.vein.api.entrypoint.FrozenVeinTypeEntrypoint;
import net.frozenblock.lib.worldgen.vein.impl.FrozenVeinType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.OreVeinifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OreVeinifier.VeinType.class)
public class VeinTypeMixin {

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static OreVeinifier.VeinType newType(String internalName, int internalId, BlockState ore, BlockState rawOreBlock, BlockState filler, int minY, int maxY) {
		throw new AssertionError("Mixin injection failed - FrozenLib VeinTypeMixin");
	}

	@SuppressWarnings("ShadowTarget")
	@Shadow
	@Final
	@Mutable
	private static OreVeinifier.VeinType[] $VALUES;

	@Inject(
			method = "<clinit>",
			at = @At(
					value = "FIELD",
					opcode = Opcodes.PUTSTATIC,
					target = "Lnet/minecraft/world/level/levelgen/OreVeinifier$VeinType;$VALUES:[Lnet/minecraft/world/level/levelgen/OreVeinifier$VeinType;",
					shift = At.Shift.AFTER
			)
	)
	private static void addCustomCategories(CallbackInfo ci) {
		var categories = new ArrayList<>(Arrays.asList($VALUES));
		var last = categories.get(categories.size() - 1);
		int currentOrdinal = last.ordinal();

		ArrayList<String> internalIds = new ArrayList<>();
		for (OreVeinifier.VeinType veinType : categories) {
			internalIds.add(veinType.name());
		}

		ArrayList<FrozenVeinType> newVeinTypes = new ArrayList<>();
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:vein_type_categories", FrozenVeinTypeEntrypoint.class).forEach(entrypoint -> {
			try {
				FrozenVeinTypeEntrypoint frozenVeinTypeEntrypoint = entrypoint.getEntrypoint();
				frozenVeinTypeEntrypoint.newCategories(newVeinTypes);
			} catch (Throwable ignored) {

			}
		});

		for (FrozenVeinType frozenVeinType : newVeinTypes) {
			var namespace = frozenVeinType.key().getNamespace();
			var path = frozenVeinType.key().getPath();
			StringBuilder internalId = new StringBuilder(namespace.toUpperCase());
			internalId.append(path.toUpperCase());
			if (internalIds.contains(internalId.toString())) {
				throw new IllegalStateException("Cannot add duplicate VeinType " + internalId + "!");
			}
			currentOrdinal += 1;
			var addedVeinType = newType(internalId.toString(), currentOrdinal, frozenVeinType.ore(), frozenVeinType.rawOreBlock(), frozenVeinType.filler(), frozenVeinType.minY(), frozenVeinType.maxY());
			categories.add(addedVeinType);
			FrozenVeinTypes.addVeinType(internalId.toString(), addedVeinType);
		}

		$VALUES = categories.toArray(new OreVeinifier.VeinType[0]);
	}
}
