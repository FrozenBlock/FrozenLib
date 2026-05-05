package net.frozenblock.lib.block.api.fire;

import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class FireTypes {
	public static final ResourceKey<FireType> FIRE = createKey(FrozenLibConstants.id("fire"));
	public static final ResourceKey<FireType> SOUL_FIRE = createKey(FrozenLibConstants.id("soul_fire"));
	public static final ResourceKey<FireType> DEFAULT = FIRE;

	public static Holder<FireType> get(RegistryAccess registryAccess, ResourceKey<FireType> id) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.FIRE_TYPE).getOrThrow(id);
	}

	public static Optional<Holder<FireType>> getTypeForBlock(RegistryAccess registryAccess, Block block) {
		final Registry<FireType> registry = registryAccess.lookupOrThrow(FrozenLibRegistries.FIRE_TYPE);
		for (FireType type : registry) {
			if (type.blocks().contains(block.builtInRegistryHolder())) return Optional.of(registry.wrapAsHolder(type));
		}
		return Optional.empty();
	}

	public static ResourceKey<FireType> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.FIRE_TYPE, id);
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		HolderSet<Block> blocks
	) {
		register(context, name, blocks, 1F, true, true, true, Optional.empty(), Optional.empty());
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		HolderSet<Block> blocks,
		float damage
	) {
		register(context, name, blocks, damage, true, true, true, Optional.empty(), Optional.empty());
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		HolderSet<Block> blocks,
		float damage,
		Identifier texture0,
		Identifier texture1
	) {
		register(context, name, blocks, damage, true, true, true, texture0, texture1);
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		HolderSet<Block> blocks,
		Identifier texture0,
		Identifier texture1
	) {
		register(context, name, blocks, 1F, true, true, true, texture0, texture1);
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		HolderSet<Block> blocks,
		float damage,
		boolean spreadsFromZombie,
		boolean spreadsFromIgniteEnchantments,
		boolean replaceable,
		Identifier texture0,
		Identifier texture1
	) {
		register(context, name, blocks, damage, spreadsFromZombie, spreadsFromIgniteEnchantments, replaceable, Optional.of(texture0), Optional.of(texture1));
	}

	public static void register(
		BootstrapContext<FireType> context,
		ResourceKey<FireType> name,
		HolderSet<Block> blocks,
		float damage,
		boolean spreadsFromZombie,
		boolean spreadsFromIgniteEnchantments,
		boolean replaceable,
		Optional<Identifier> texture0,
		Optional<Identifier> texture1
	) {
		context.register(name, new FireType(blocks, damage, spreadsFromZombie, spreadsFromIgniteEnchantments, replaceable, texture0, texture1));
	}

	public static void bootstrap(BootstrapContext<FireType> context) {
		final HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

		register(context, FIRE, blocks.getOrThrow(FrozenLibBlockTags.DEFAULT_FIRE_BLOCKS));
		register(context, SOUL_FIRE, blocks.getOrThrow(FrozenLibBlockTags.SOUL_FIRE_BLOCKS), 2F, Identifier.withDefaultNamespace("soul_fire_0"), Identifier.withDefaultNamespace("soul_fire_1"));
	}
}
