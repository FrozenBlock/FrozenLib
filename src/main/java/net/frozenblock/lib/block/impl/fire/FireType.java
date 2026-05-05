package net.frozenblock.lib.block.impl.fire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.level.block.Block;

public record FireType(
	HolderSet<Block> blocks,
	float damage,
	boolean spreadsFromZombie,
	boolean spreadsFromIgniteEnchantments,
	boolean replaceable,
	Optional<Identifier> texture0,
	Optional<Identifier> texture1
) {
	public static final Codec<FireType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(FireType::blocks),
		Codec.FLOAT.fieldOf("damage").forGetter(FireType::damage),
		Codec.BOOL.fieldOf("spreads_from_zombie").forGetter(FireType::spreadsFromZombie),
		Codec.BOOL.fieldOf("spreads_from_ignite_enchantments").forGetter(FireType::spreadsFromIgniteEnchantments),
		Codec.BOOL.fieldOf("replaceable").forGetter(FireType::replaceable),
		Identifier.CODEC.optionalFieldOf("texture_0").forGetter(FireType::texture0),
		Identifier.CODEC.optionalFieldOf("texture_1").forGetter(FireType::texture1)
	).apply(instance, FireType::new));
	public static final Codec<Holder<FireType>> CODEC = RegistryFixedCodec.create(FrozenLibRegistries.FIRE_TYPE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FireType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FrozenLibRegistries.FIRE_TYPE);
}
