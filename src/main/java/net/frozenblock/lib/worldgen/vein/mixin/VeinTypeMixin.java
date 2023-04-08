/*
 * Copyright 2023 FrozenBlock
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
