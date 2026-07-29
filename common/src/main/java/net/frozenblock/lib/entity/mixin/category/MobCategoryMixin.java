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
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenblock.lib.entity.api.category.MobCategoryApiEntrypoint;
import net.frozenblock.lib.entrypoint.api.EntrypointHelper;
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
	private static MobCategory frozenLib$newMobCategory(
		String internalName,
		int ordinal,
		String name,
		String debugAbbreviation,
		int max,
		boolean isFriendly,
		boolean isPersistent,
		int despawnDistance
	) {
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
		final AtomicInteger currentOrdinal = new AtomicInteger(last.ordinal());

		// Store all internal names to check for duplicates later
		final ArrayList<String> allInternalNames = new ArrayList<>();
		for (MobCategory category : categories) allInternalNames.add(category.name());

		// Add new categories
		final MobCategoryApiEntrypoint.Context context = new MobCategoryApiEntrypoint.Context();
		EntrypointHelper.forEachEntrypoint(MobCategoryApiEntrypoint.class, entrypoint -> entrypoint.add(context));

		context.forEach(mobCategory -> {
			final String internalName = mobCategory.createNameWithModId("$").toUpperCase();
			if (internalName.equals("$")) throw new IllegalStateException("Cannot add MobCategory with empty internal name!");
			if (allInternalNames.stream().anyMatch(string -> string.equals(internalName))) {
				throw new IllegalStateException("Cannot add duplicate MobCategory " + internalName + "!");
			}

			final MobCategory newMobCategory = frozenLib$newMobCategory(
				internalName,
				currentOrdinal.incrementAndGet(),
				mobCategory.createNameWithModId(":").toLowerCase(),
				mobCategory.debugAbbreviation(),
				mobCategory.max(),
				mobCategory.isFriendly(),
				mobCategory.isPersistent(),
				mobCategory.despawnDistance()
			);
			mobCategory.onCreated(newMobCategory);
			categories.add(newMobCategory);
		});

		$VALUES = categories.toArray(new MobCategory[0]);
	}
}
