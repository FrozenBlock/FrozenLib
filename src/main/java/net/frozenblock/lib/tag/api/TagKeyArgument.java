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

package net.frozenblock.lib.tag.api;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TagKeyArgument<T> implements ArgumentType<TagKeyArgument.Result<T>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
	final ResourceKey<? extends Registry<T>> registryKey;

	public TagKeyArgument(ResourceKey<? extends Registry<T>> registryKey) {
		this.registryKey = registryKey;
	}

	public static <T> TagKeyArgument<T> tagKey(ResourceKey<? extends Registry<T>> registryKey) {
		return new TagKeyArgument<>(registryKey);
	}

	public static <T> TagKeyArgument.Result<T> getTagKey(
		CommandContext<CommandSourceStack> context, String argument, ResourceKey<Registry<T>> registryKey, DynamicCommandExceptionType dynamicCommandExceptionType
	) throws CommandSyntaxException {
		TagKeyArgument.Result<?> result = context.getArgument(argument, TagKeyArgument.Result.class);
		Optional<TagKeyArgument.Result<T>> optional = result.cast(registryKey);
		return optional.orElseThrow(() -> dynamicCommandExceptionType.create(result));
	}

	public TagKeyArgument.Result<T> parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();

		try {
			reader.skip();
			ResourceLocation resourceLocation = ResourceLocation.read(reader);
			return new Result<>(TagKey.create(this.registryKey, resourceLocation));
		} catch (CommandSyntaxException var4) {
			reader.setCursor(cursor);
			throw var4;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		Object var4 = commandContext.getSource();
		return var4 instanceof SharedSuggestionProvider sharedSuggestionProvider
			? sharedSuggestionProvider.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.TAGS, suggestionsBuilder, commandContext)
			: suggestionsBuilder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Info<T> implements ArgumentTypeInfo<TagKeyArgument<T>, TagKeyArgument.Info<T>.Template> {
		public void serializeToNetwork(TagKeyArgument.Info<T>.Template template, FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(template.registryKey.location());
		}

		public TagKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			ResourceLocation resourceLocation = buffer.readResourceLocation();
			return new TagKeyArgument.Info.Template(ResourceKey.createRegistryKey(resourceLocation));
		}

		public void serializeToJson(TagKeyArgument.Info<T>.Template template, JsonObject json) {
			json.addProperty("registry", template.registryKey.location().toString());
		}

		public TagKeyArgument.Info<T>.Template unpack(TagKeyArgument<T> argument) {
			return new TagKeyArgument.Info.Template(argument.registryKey);
		}

		public final class Template implements ArgumentTypeInfo.Template<TagKeyArgument<T>> {
			final ResourceKey<? extends Registry<T>> registryKey;

			Template(ResourceKey<? extends Registry<T>> registryKey) {
				this.registryKey = registryKey;
			}

			public TagKeyArgument<T> instantiate(CommandBuildContext context) {
				return new TagKeyArgument<>(this.registryKey);
			}

			@Override
			public ArgumentTypeInfo<TagKeyArgument<T>, ?> type() {
				return TagKeyArgument.Info.this;
			}
		}
	}

	record Result<T>(TagKey<T> key) {
		public <E> Optional<TagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> registryKey) {
			return this.key.cast(registryKey).map(Result::new);
		}

		public String asPrintable() {
			return "#" + this.key.location();
		}
	}
}
