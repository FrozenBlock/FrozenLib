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

package net.frozenblock.lib.mobcategory.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.mobcategory.api.FrozenMobCategories;
import net.frozenblock.lib.mobcategory.api.entrypoint.FrozenMobCategoryEntrypoint;
import net.frozenblock.lib.mobcategory.impl.FrozenMobCategory;
import net.minecraft.world.entity.MobCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobCategory.class)
public class MobCategoryMixin {

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static MobCategory newType(String internalName, int internalId, String name, int max, boolean isFriendly, boolean isPersistent, int despawnDistance) {
		throw new AssertionError("Mixin injection failed - FrozenLib MobCategoryMixin");
	}

	@SuppressWarnings("ShadowTarget")
	@Shadow
	@Final
	@Mutable
	private static MobCategory[] $VALUES;

	@Inject(
			method = "<clinit>",
			at = @At(
					value = "FIELD",
					opcode = Opcodes.PUTSTATIC,
					target = "Lnet/minecraft/world/entity/MobCategory;$VALUES:[Lnet/minecraft/world/entity/MobCategory;",
					shift = At.Shift.AFTER
			)
	)
	private static void addCustomCategories(CallbackInfo ci) {
		var categories = new ArrayList<>(Arrays.asList($VALUES));
		var last = categories.get(categories.size() - 1);
		int currentOrdinal = last.ordinal();

		ArrayList<String> internalIds = new ArrayList<>();
		for (MobCategory category : categories) {
			internalIds.add(category.name());
		}

		ArrayList<FrozenMobCategory> newCategories = new ArrayList<>();
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:mob_categories", FrozenMobCategoryEntrypoint.class).forEach(entrypoint -> {
			try {
				FrozenMobCategoryEntrypoint mobCategoryEntrypoint = entrypoint.getEntrypoint();
				mobCategoryEntrypoint.newCategories(newCategories);
			} catch (Throwable ignored) {

			}
		});

		for (FrozenMobCategory category : newCategories) {
			var namespace = category.key().getNamespace();
			var path = category.key().getPath();
			StringBuilder internalId = new StringBuilder(namespace.toUpperCase());
			internalId.append(path.toUpperCase());
			if (internalIds.contains(internalId.toString())) {
				throw new IllegalStateException("Cannot add duplicate MobCategory " + internalId + "!");
			}
			currentOrdinal += 1;
			var addedCategory = newType(internalId.toString(), currentOrdinal, namespace + path, category.max(), category.isFriendly(), category.isPersistent(), category.despawnDistance());
			categories.add(addedCategory);
			FrozenMobCategories.addMobCategory(internalId.toString(), addedCategory);
		}

		$VALUES = categories.toArray(new MobCategory[0]);
	}
}
