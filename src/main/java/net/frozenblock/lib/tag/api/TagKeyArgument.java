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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
		CommandContext<CommandSourceStack> context, String argument, ResourceKey<Registry<T>> key, DynamicCommandExceptionType exceptionType
	) throws CommandSyntaxException {
		final TagKeyArgument.Result<?> result = context.getArgument(argument, TagKeyArgument.Result.class);
		final Optional<TagKeyArgument.Result<T>> optional = result.cast(key);
		return optional.orElseThrow(() -> exceptionType.create(result));
	}

	public TagKeyArgument.Result<T> parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();

		try {
			reader.skip();
			Identifier identifier = Identifier.read(reader);
			return new Result<>(TagKey.create(this.registryKey, identifier));
		} catch (CommandSyntaxException var4) {
			reader.setCursor(cursor);
			throw var4;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestions) {
		final Object source = context.getSource();
		return source instanceof SharedSuggestionProvider sharedSuggestionProvider
			? sharedSuggestionProvider.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.TAGS, suggestions, context)
			: suggestions.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Info<T> implements ArgumentTypeInfo<TagKeyArgument<T>, TagKeyArgument.Info<T>.Template> {

		@Override
		public void serializeToNetwork(TagKeyArgument.Info<T>.Template template, FriendlyByteBuf buffer) {
			buffer.writeIdentifier(template.registryKey.identifier());
		}

		@Override
		public TagKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			Identifier identifier = buffer.readIdentifier();
			return new TagKeyArgument.Info.Template(ResourceKey.createRegistryKey(identifier));
		}

		@Override
		public void serializeToJson(TagKeyArgument.Info<T>.Template template, JsonObject json) {
			json.addProperty("registry", template.registryKey.identifier().toString());
		}

		@Override
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
