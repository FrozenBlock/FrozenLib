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

package net.frozenblock.lib.datafix.mixin;

import com.mojang.datafixers.DSL;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.datafix.api.FrozenDataFixTypes;
import net.frozenblock.lib.datafix.api.entrypoint.FrozenDataFixTypesEntrypoint;
import net.frozenblock.lib.datafix.impl.FrozenDataFixType;
import net.minecraft.util.datafix.DataFixTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixTypes.class)
public class DataFixTypesMixin {
	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static DataFixTypes newType(String internalName, int internalId, DSL.TypeReference type) {
		throw new AssertionError("Mixin injection failed - FrozenLib DataFixTypesMixin");
	}

	@SuppressWarnings("ShadowTarget")
	@Shadow
	@Final
	@Mutable
	private static DataFixTypes[] $VALUES;

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "FIELD",
			opcode = Opcodes.PUTSTATIC,
			target = "Lnet/minecraft/util/datafix/DataFixTypes;$VALUES:[Lnet/minecraft/util/datafix/DataFixTypes;",
			shift = At.Shift.AFTER
		)
	)
	private static void addCustomCategories(CallbackInfo ci) {
		var dataFixTypes = new ArrayList<>(Arrays.asList($VALUES));
		var last = dataFixTypes.get(dataFixTypes.size() - 1);
		int currentOrdinal = last.ordinal();

		ArrayList<String> internalIds = new ArrayList<>();
		for (DataFixTypes category : dataFixTypes) {
			internalIds.add(category.name());
		}

		ArrayList<FrozenDataFixType> newDataFixTypes = new ArrayList<>();
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:data_fix_types", FrozenDataFixTypesEntrypoint.class).forEach(entrypoint -> {
			try {
				FrozenDataFixTypesEntrypoint dataFixTypesEntrypoint = entrypoint.getEntrypoint();
				dataFixTypesEntrypoint.newCategories(newDataFixTypes);
			} catch (Throwable ignored) {

			}
		});

		for (FrozenDataFixType dataFixType : newDataFixTypes) {
			var namespace = dataFixType.key().getNamespace();
			var path = dataFixType.key().getPath();
			StringBuilder internalId = new StringBuilder(namespace.toUpperCase());
			internalId.append(path.toUpperCase());
			if (internalIds.contains(internalId.toString())) {
				throw new IllegalStateException("Cannot add duplicate DataFixTypes " + internalId + "!");
			}
			currentOrdinal += 1;
			var addedDataFixType = newType(internalId.toString(), currentOrdinal, dataFixType.type());
			dataFixTypes.add(addedDataFixType);
			FrozenDataFixTypes.addDataFixType(internalId.toString(), addedDataFixType);
		}

		$VALUES = dataFixTypes.toArray(new DataFixTypes[0]);
	}

}
