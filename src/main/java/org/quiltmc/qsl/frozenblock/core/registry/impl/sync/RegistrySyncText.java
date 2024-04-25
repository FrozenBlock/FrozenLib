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

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocolDef;

public class RegistrySyncText {

	private static <T> MutableComponent entryList(List<T> namespacesList, Function<T, Component> toText) {
		var namespaceText = Component.empty();

		var textLength = 0;
		var lines = 0;

		while (lines < 2 && !namespacesList.isEmpty()) {
			var max = lines == 0 ? 38 : 30;
			while (textLength < max && !namespacesList.isEmpty()) {
				var t = toText.apply(namespacesList.remove(0));
				namespaceText.append(t);

				textLength += t.getString().length();

				if (!namespacesList.isEmpty()) {
					var alt = (lines + toText.apply(namespacesList.get(0)).getString().length() < max && lines == 1);
					if (namespacesList.size() == 1 || alt) {
						namespaceText.append(Component.translatableWithFallback("frozenlib.core.registry_sync.and", " and ").withStyle(alt ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
						textLength += 6;
					} else {
						namespaceText.append(Component.literal(", ").withStyle(ChatFormatting.DARK_GRAY));
						textLength += 2;
					}
				}
			}

			if (!namespacesList.isEmpty() && lines != 1) {
				namespaceText.append("\n");
			}

			textLength = 0;
			lines++;
		}

		if (!namespacesList.isEmpty()) {
			namespaceText.append(Component.translatableWithFallback("frozenlib.core.registry_sync.more",  "%s more...", namespacesList.size()));
		}

		return namespaceText;
	}

	public static Component unsupportedModVersion(List<ModProtocolDef> unsupported, ModProtocolDef missingPrioritized) {
		if (missingPrioritized != null && !missingPrioritized.versions().isEmpty()) {
			var x = Component.translatableWithFallback("frozenlib.core.registry_sync.require_main_mod_protocol", "This server requires %s with protocol version of %s!",
				Component.literal(missingPrioritized.displayName()).withStyle(ChatFormatting.YELLOW),
				missingPrioritized.versions().getInt(0)
			);

			if (ModProtocol.enabled && ModProtocol.prioritizedEntry != null) {
				x.append("\n").append(
					Component.translatableWithFallback("frozenlib.core.registry_sync.main_mod_protocol", "You are on %s with protocol version of %s.",
						Component.literal(ModProtocol.prioritizedEntry.displayName()).withStyle(ChatFormatting.GOLD), ModProtocol.prioritizedEntry.versions().getInt(0)
					)
				);
			}

			return x;
		} else {
			System.out.println(unsupported.size());
			var namespacesList = new ArrayList<>(unsupported);
			namespacesList.sort(Comparator.comparing(ModProtocolDef::displayName));
			var namespaceText = entryList(namespacesList, RegistrySyncText::protocolDefEntryText).withStyle(ChatFormatting.GRAY);

			return Component.translatableWithFallback("frozenlib.core.registry_sync.unsupported_mod_protocol", "Unsupported mod protocol versions for:\n%s",
				namespaceText
			);
		}
	}

	private static Component protocolDefEntryText(ModProtocolDef def) {
		MutableComponent version;
		var x = def.versions();
		if (x.isEmpty()) {
			version = Component.literal("WHAT??? HOW???");
		} else if (x.size() == 1) {
			version = Component.literal("" + x.getInt(0));
		} else {
			version = (MutableComponent) ComponentUtils.formatList(x.subList(0, Math.min(x.size(), 4)), n -> Component.literal(n.toString()));
			if (x.size() > 4) {
				version = version.append(ComponentUtils.DEFAULT_SEPARATOR).append("...");
			}
		}

		return Component.translatableWithFallback("quilt.core.registry_sync.protocol_entry", "%s (%s)", def.displayName(), version);
	}
}
