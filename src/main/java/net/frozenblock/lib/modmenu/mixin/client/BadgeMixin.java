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
	@Shadow
	@Final
	@Mutable
	private static Mod.Badge[] $VALUES;

	@Inject(
			method = "<clinit>",
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
