package net.frozenblock.lib.tag.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public class TagListCommand {
	private TagListCommand() {}

	private static final DynamicCommandExceptionType ERROR_TAG_INVALID = new DynamicCommandExceptionType(
		type -> Component.translatable("commands.frozenlib.taglist.tag.invalid", type)
	);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("taglist")
				.requires(source -> source.hasPermission(2))
				.then(
					Commands.literal("biome")
						.then(
							Commands.argument("biome", TagKeyArgument.tagKey(Registries.BIOME))
								.executes(
									context -> list(
										context.getSource(),
										Registries.BIOME,
										TagKeyArgument.getTagKey(context, "biome", Registries.BIOME, ERROR_TAG_INVALID)
									)
								)
						)
				)
				.then(
					Commands.literal("block")
						.then(
							Commands.argument("block", TagKeyArgument.tagKey(Registries.BLOCK))
								.executes(
									context -> list(
										context.getSource(),
										Registries.BLOCK,
										TagKeyArgument.getTagKey(context, "block", Registries.BLOCK, ERROR_TAG_INVALID)
									)
								)
						)
				)
				.then(
					Commands.literal("entity_type")
						.then(
							Commands.argument("entity_type", TagKeyArgument.tagKey(Registries.ENTITY_TYPE))
								.executes(
									context -> list(
										context.getSource(),
										Registries.ENTITY_TYPE,
										TagKeyArgument.getTagKey(context, "entity_type", Registries.ENTITY_TYPE, ERROR_TAG_INVALID)
									)
								)
						)
				)
				.then(
					Commands.literal("fluid")
						.then(
							Commands.argument("fluid", TagKeyArgument.tagKey(Registries.FLUID))
								.executes(
									context -> list(
										context.getSource(),
										Registries.FLUID,
										TagKeyArgument.getTagKey(context, "fluid", Registries.FLUID, ERROR_TAG_INVALID)
									)
								)
						)
				)
				.then(
					Commands.literal("instrument")
						.then(
							Commands.argument("instrument", TagKeyArgument.tagKey(Registries.INSTRUMENT))
								.executes(
									context -> list(
										context.getSource(),
										Registries.INSTRUMENT,
										TagKeyArgument.getTagKey(context, "instrument", Registries.INSTRUMENT, ERROR_TAG_INVALID)
									)
								)
						)
				)
				.then(
					Commands.literal("item")
						.then(
							Commands.argument("item", TagKeyArgument.tagKey(Registries.ITEM))
								.executes(
									context -> list(
										context.getSource(),
										Registries.ITEM,
										TagKeyArgument.getTagKey(context, "item", Registries.ITEM, ERROR_TAG_INVALID)
									)
								)
						)
				)
				.then(
					Commands.literal("structure")
						.then(
							Commands.argument("structure", TagKeyArgument.tagKey(Registries.STRUCTURE))
								.executes(
									context -> list(
										context.getSource(),
										Registries.STRUCTURE,
										TagKeyArgument.getTagKey(context, "structure", Registries.STRUCTURE, ERROR_TAG_INVALID)
									)
								)
						)
				)
		);
	}

	private static <T> int list(CommandSourceStack source, ResourceKey<Registry<T>> registryKey, TagKeyArgument.Result<T> tag) throws CommandSyntaxException {
		Registry<T> registry = source.getLevel().registryAccess().registryOrThrow(registryKey);
		String printable = tag.asPrintable();
		HolderSet.Named<T> holderSet = registry.getTag(tag.key()).orElseThrow(
			() -> ERROR_TAG_INVALID.create(printable)
		);
		int size = holderSet.size();

		for (Holder<T> value : holderSet) {
			if (holderSet.contains(value)) {
				source.sendSuccess(
					() -> Component.literal(
						value.unwrapKey().orElseThrow().location().toString()
					),
					true
				);
			}
		}
		source.sendSuccess(
			() -> Component.translatable("commands.frozenlib.taglist.footer", size, printable),
			true
		);
		return size;
	}
}
