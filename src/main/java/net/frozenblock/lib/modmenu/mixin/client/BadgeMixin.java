/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.modmenu.mixin.client;

import com.terraformersmc.modmenu.util.mod.Mod;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.modmenu.api.FrozenModMenuEntrypoint;
import net.frozenblock.lib.modmenu.impl.FrozenModMenuBadge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mod.Badge.class)
public class BadgeMixin {

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static Mod.Badge newType(String internalName, int internalId, String translationKey, int outlineColor, int fillColor, String key) {
		throw new AssertionError("Mixin injection failed - FrozenLib BadgeMixin");
	}

	@SuppressWarnings("ShadowTarget")
	@Shadow(remap = false)
	@Final
	@Mutable
	private static Mod.Badge[] $VALUES;

	@Inject(
			method = "<clinit>",
			remap = false,
			at = @At(
					value = "FIELD",
					opcode = Opcodes.PUTSTATIC,
					target = "Lcom/terraformersmc/modmenu/util/mod/Mod$Badge;$VALUES:[Lcom/terraformersmc/modmenu/util/mod/Mod$Badge;",
					shift = At.Shift.AFTER
			)
	)
	private static void addCustomBadges(CallbackInfo ci) {
		var badges = new ArrayList<>(Arrays.asList($VALUES));
		var last = badges.get(badges.size() - 1);
		int currentOrdinal = last.ordinal() + 1;

		var frozenblock = newType("FROZENBLOCK", currentOrdinal, "FrozenBlock", 0xFF84A0FE, 0xFF000D5B, "frozenblock");
		currentOrdinal += 1;
		var indev = newType("INDEV", currentOrdinal, "Indev", 0xFF71BC00, 0xFF1C5400, "indev");
		badges.add(frozenblock);
		badges.add(indev);

		ArrayList<String> internalIds = new ArrayList<>();
		for (Mod.Badge badge : badges) {
			internalIds.add(badge.name());
		}

		ArrayList<FrozenModMenuBadge> newBadges = new ArrayList<>();
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:modmenu_badges", FrozenModMenuEntrypoint.class).forEach(entrypoint -> {
			try {
				FrozenModMenuEntrypoint frozenModMenuEntrypoint = entrypoint.getEntrypoint();
				newBadges.addAll(frozenModMenuEntrypoint.newBadges());
			} catch (Throwable ignored) {

			}
		});

		for (FrozenModMenuBadge newBadge : newBadges) {
			currentOrdinal += 1;
			StringBuilder internalId = new StringBuilder(newBadge.key.toUpperCase());
			while (internalIds.contains(internalId.toString())) {
				internalId.append("A");
			}
			var addedBadge = newType(internalId.toString(), currentOrdinal, newBadge.translationKey, newBadge.outlineColor, newBadge.fillColor, newBadge.key);
			badges.add(addedBadge);
		}

		$VALUES = badges.toArray(new Mod.Badge[0]);
	}

}
