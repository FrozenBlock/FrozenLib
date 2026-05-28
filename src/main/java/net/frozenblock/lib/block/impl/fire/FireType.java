/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.impl.fire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
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
	Optional<Identifier> texture1,
	Optional<ConfigPredicate> enabled
) {
	public static final Codec<FireType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(FireType::blocks),
		Codec.FLOAT.fieldOf("damage").forGetter(FireType::damage),
		Codec.BOOL.fieldOf("spreads_from_zombie").forGetter(FireType::spreadsFromZombie),
		Codec.BOOL.fieldOf("spreads_from_ignite_enchantments").forGetter(FireType::spreadsFromIgniteEnchantments),
		Codec.BOOL.fieldOf("replaceable").forGetter(FireType::replaceable),
		Identifier.CODEC.optionalFieldOf("texture_0").forGetter(FireType::texture0),
		Identifier.CODEC.optionalFieldOf("texture_1").forGetter(FireType::texture1),
		ConfigPredicate.CODEC.optionalFieldOf("config_predicate").forGetter(FireType::enabled)
	).apply(instance, FireType::new));
	public static final Codec<Holder<FireType>> CODEC = RegistryFixedCodec.create(FrozenLibRegistries.FIRE_TYPE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FireType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FrozenLibRegistries.FIRE_TYPE);

	public boolean isEnabled() {
		return this.enabled.map(ConfigPredicate::test).orElse(true);
	}
}
