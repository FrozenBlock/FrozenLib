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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class TagArgument implements SerializableArgument<HolderSet.Named<?>> {
	private static final MapCodec<TagArgument> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceKey.REGISTRY_CODEC.fieldOf("registry").forGetter(argument -> argument.registryKey)
	).apply(instance, TagArgument::new));
	private static final StreamCodec<ByteBuf, TagArgument> STREAM_CODEC = StreamCodec.composite(
		ResourceKey.REGISTRY_STREAM_CODEC, argument -> argument.registryKey, TagArgument::new
	);
	public static final SerializableArgument.Type<TagArgument> TYPE = new SerializableArgument.Type<>(MAP_CODEC, STREAM_CODEC);
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "skeletons", "minecraft:skeletons");
	private final ResourceKey<? extends Registry<?>> registryKey;

	public TagArgument(ResourceKey<? extends Registry<?>> registryKey) {
		this.registryKey = registryKey;
	}

	@Override
	public SerializableArgument.Type<TagArgument> type() {
		return TYPE;
	}

	public static ArgumentType<HolderSet.Named<?>> tag(ResourceKey<? extends Registry<?>> registryKey) {
		return new TagArgument(registryKey).toBrigadier();
	}

	public static <T> HolderSet.Named<T> getTag(
		CommandContext<CommandSourceStack> context,
		String name,
		ResourceKey<Registry<T>> registryKey
	) throws CommandSyntaxException {
		final HolderSet.Named<?> argument = context.getArgument(name, HolderSet.Named.class);
		final Optional<HolderSet.Named<T>> value = argument.key().isFor(registryKey) ? Optional.of((HolderSet.Named<T>) argument) : Optional.empty();

		final TagKey<?> tagKey = argument.key();
		return value.orElseThrow(() -> ResourceOrTagArgument.ERROR_INVALID_TAG_TYPE.create(tagKey.location(), tagKey.registry(), registryKey.identifier()));
	}

	@Override
	public HolderSet.Named<?> parse(StringReader reader, ParseContext context) throws CommandSyntaxException {
		int cursor = reader.getCursor();

		try {
			reader.skip();
			final Identifier tagId = Identifier.read(reader);
			return context.holderGetters()
				.get(TagKey.create(this.registryKey, tagId))
				.orElseThrow(() -> ResourceOrTagArgument.ERROR_UNKNOWN_TAG.createWithContext(reader, tagId, this.registryKey.identifier()));
		} catch (CommandSyntaxException exception) {
			reader.setCursor(cursor);
			throw exception;
		}
	}

	@Override
	public CompletableFuture<Suggestions> listSuggestions(CommandContext<? extends SuggestionContext> context, SuggestionsBuilder builder) {
		return context.getSource().suggestRegistryElements(context, this.registryKey, SuggestionContext.ElementSuggestionType.TAGS_WITHOUT_PREFIX, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
