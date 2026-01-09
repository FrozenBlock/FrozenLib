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

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.client;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import net.minecraft.ChatFormatting;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class LogBuilder {
	private Component title;
	private List<Component> entriesCurrent;
	private List<Section> sections = new ArrayList<>();
	private Component currentText = null;
	private int duplicateCount = 0;

	private static Component duplicatedText(Component currentText, int duplicateCount) {
		if (duplicateCount < 2) return currentText;
		return Component.empty().append(currentText).append(Component.literal(" (" + duplicateCount + ")").withStyle(ChatFormatting.BLUE));
	}

	public static String stringify(List<Section> sections) {
		final var builder = new StringBuilder();
		final var sectionIterator = sections.iterator();
		while (sectionIterator.hasNext()) {
			final var entry = sectionIterator.next();
			builder.append("## " + entry.title().getString());

			if (!entry.entries.isEmpty()) {
				builder.append("\n");
				final var entryIteration = entry.entries.iterator();
				while (entryIteration.hasNext()) {
					builder.append("   " + entryIteration.next().getString());
					if (entryIteration.hasNext()) builder.append("\n");
				}
			}

			if (sectionIterator.hasNext()) builder.append("\n");
		}

		return builder.toString();
	}

	public void pushT(String id, String lang, Object... args) {
		this.push(Component.translatableWithFallback("frozenlib.core.registry_sync.log." + id, lang, args));
	}

	public void push(Component title) {
		if (this.title != null) this.sections.add(new Section(this.title, this.entriesCurrent));
		this.title = title;
		this.entriesCurrent = new ArrayList<>();
	}

	public void textEntry(Component text) {
		this.text(Component.literal("- ").append(Component.empty().append(text)).withStyle(ChatFormatting.GRAY));
	}

	public void text(Component text) {
		if (this.currentText != null && !text.equals(this.currentText)) {
			this.entriesCurrent.add(duplicatedText(this.currentText, this.duplicateCount));
			this.duplicateCount = 1;
		} else {
			this.duplicateCount++;
		}

		this.currentText = text;
	}

	public List<Section> finish() {
		if (this.title != null) {
			final var entries = new ArrayList<>(this.entriesCurrent);
			if (this.currentText != null) entries.add(duplicatedText(this.currentText, this.duplicateCount));
			this.sections.add(new Section(this.title, entries));
		}

		final var sections = this.sections;
		this.clear();
		return sections;
	}

	public void clear() {
		this.sections = new ArrayList<>();
		this.title = null;
		this.entriesCurrent = null;
	}

	public String asString() {
		final var sections = new ArrayList<>(this.sections);
		if (this.title != null) sections.add(new Section(this.title, this.entriesCurrent));
		return stringify(sections);
	}

	public record Section(Component title, List<Component> entries) {
	}
}
