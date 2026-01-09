/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.entity.mixin.category;

import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entity.api.category.FrozenMobCategories;
import net.frozenblock.lib.entity.api.category.entrypoint.FrozenMobCategoryEntrypoint;
import net.frozenblock.lib.entity.impl.category.FrozenMobCategory;
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
	private static void frozenLib$addCustomCategories(CallbackInfo info) {
		final var categories = new ArrayList<>(Arrays.asList($VALUES));
		final var last = categories.getLast();
		int currentOrdinal = last.ordinal();

		final ArrayList<String> internalIds = new ArrayList<>();
		for (MobCategory category : categories) internalIds.add(category.name());

		final ArrayList<FrozenMobCategory> newCategories = new ArrayList<>();
		FabricLoader.getInstance()
			.getEntrypointContainers("frozenlib:mob_categories", FrozenMobCategoryEntrypoint.class)
			.forEach(entrypoint -> {
				try {
					FrozenMobCategoryEntrypoint mobCategoryEntrypoint = entrypoint.getEntrypoint();
					mobCategoryEntrypoint.newCategories(newCategories);
				} catch (Throwable ignored) {
				}
			});

		for (FrozenMobCategory category : newCategories) {
			final var namespace = category.key().getNamespace();
			final var path = category.key().getPath();
			final StringBuilder internalId = new StringBuilder(namespace.toUpperCase());
			internalId.append(path.toUpperCase());
			if (internalIds.contains(internalId.toString())) throw new IllegalStateException("Cannot add duplicate MobCategory " + internalId + "!");

			currentOrdinal += 1;
			final var addedCategory = newType(internalId.toString(), currentOrdinal, namespace + path, category.max(), category.isFriendly(), category.isPersistent(), category.despawnDistance());
			categories.add(addedCategory);
			FrozenMobCategories.addMobCategory(internalId.toString(), addedCategory);
		}

		$VALUES = categories.toArray(new MobCategory[0]);
	}
}
