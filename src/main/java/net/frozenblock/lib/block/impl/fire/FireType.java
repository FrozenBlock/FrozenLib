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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public record FireType(
	SourceSettings sourceSettings,
	DamageSettings damageSettings,
	SpreadSettings spreadSettings,
	TextureSettings textures,
	Optional<ConfigPredicate> enabledWhen
) {
	public static final Codec<FireType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		SourceSettings.CODEC.fieldOf("source_settings").forGetter(FireType::sourceSettings),
		DamageSettings.CODEC.fieldOf("damage_settings").forGetter(FireType::damageSettings),
		SpreadSettings.CODEC.fieldOf("spread_settings").forGetter(FireType::spreadSettings),
		TextureSettings.CODEC.fieldOf("textures").forGetter(FireType::textures),
		ConfigPredicate.CODEC.optionalFieldOf("config_predicate").forGetter(FireType::enabledWhen)
	).apply(instance, FireType::new));
	public static final Codec<Holder<FireType>> CODEC = RegistryFixedCodec.create(FrozenLibRegistries.FIRE_TYPE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<FireType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FrozenLibRegistries.FIRE_TYPE);

	public static Builder builder() {
		return new Builder();
	}

	public boolean isEnabled() {
		return this.enabledWhen.map(ConfigPredicate::test).orElse(true);
	}

	public record SourceSettings(
		HolderSet<Block> fireSourceBlocks,
		HolderSet<Block> supportingBlocks
	) {
		public static final Codec<SourceSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("fire_source_blocks").forGetter(SourceSettings::fireSourceBlocks),
			RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("supporting_blocks", HolderSet.empty()).forGetter(SourceSettings::supportingBlocks)
		).apply(instance, SourceSettings::new));
	}

	public record DamageSettings(
		float damage,
		float vulnerableDamage,
		HolderSet<EntityType<?>> vulnerableEntityTypes,
		HolderSet<EntityType<?>> damageImmuneEntityTypes
	) {
		public static final Codec<DamageSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("damage", 1F).forGetter(DamageSettings::damage),
			Codec.FLOAT.optionalFieldOf("vulnerable_damage", 1F).forGetter(DamageSettings::vulnerableDamage),
			RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("vulnerable_entity_types", HolderSet.empty()).forGetter(DamageSettings::vulnerableEntityTypes),
			RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("damage_immune_entity_types", HolderSet.empty()).forGetter(DamageSettings::damageImmuneEntityTypes)
		).apply(instance, DamageSettings::new));
	}

	public record SpreadSettings(
		boolean spreadsFromZombieAttack,
		boolean spreadsFromIgniteEnchantments,
		boolean replaceableByOtherFireTypes,
		HolderSet<EntityType<?>> alwaysApplyToEntityTypes,
		HolderSet<EntityType<?>> cannotApplyToEntityTypes
	) {
		public static final Codec<SpreadSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("spreads_from_zombie_attack", true).forGetter(SpreadSettings::spreadsFromZombieAttack),
			Codec.BOOL.optionalFieldOf("spreads_from_ignite_enchantments", true).forGetter(SpreadSettings::spreadsFromIgniteEnchantments),
			Codec.BOOL.optionalFieldOf("replaceable_by_other_fire_types", true).forGetter(SpreadSettings::replaceableByOtherFireTypes),
			RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("always_apply_to_entity_types", HolderSet.empty()).forGetter(SpreadSettings::alwaysApplyToEntityTypes),
			RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("cannot_apply_to_entity_types", HolderSet.empty()).forGetter(SpreadSettings::cannotApplyToEntityTypes)
		).apply(instance, SpreadSettings::new));
	}

	public record TextureSettings(
		Optional<Identifier> texture0,
		Optional<Identifier> texture1
	) {
		public static final Codec<TextureSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("texture_0").forGetter(TextureSettings::texture0),
			Identifier.CODEC.optionalFieldOf("texture_1").forGetter(TextureSettings::texture1)
		).apply(instance, TextureSettings::new));
	}

	public static class Builder {
		// SOURCE SETTINGS
		private HolderSet<Block> fireSourceBlocks = HolderSet.empty();
		private HolderSet<Block> supportingBlocks = HolderSet.empty();
		// DAMAGE SETTINGS
		private float damage = 1F;
		private float vulnerableDamage = 1F;
		private HolderSet<EntityType<?>> vulnerableEntityTypes = HolderSet.empty();
		private HolderSet<EntityType<?>> damageImmuneEntityTypes = HolderSet.empty();
		// SPREAD SETTINGS
		private boolean spreadsFromZombieAttack = true;
		private boolean spreadsFromIgniteEnchantments = true;
		private boolean replaceableByOtherFireTypes = true;
		private HolderSet<EntityType<?>> alwaysApplyToEntityTypes = HolderSet.empty();
		private HolderSet<EntityType<?>> cannotApplyToEntityTypes = HolderSet.empty();
		// TEXTURES
		private Identifier texture0 = null;
		private Identifier texture1 = null;
		// ENABLED
		private ConfigPredicate enabledWhen = null;

		private Builder() {}

		public Builder fireSourceBlocks(HolderSet<Block> fireSourceBlocks) {
			this.fireSourceBlocks = fireSourceBlocks;
			return this;
		}

		public Builder supportingBlocks(HolderSet<Block> supportingBlocks) {
			this.supportingBlocks = supportingBlocks;
			return this;
		}

		public Builder damage(float damage) {
			this.damage = damage;
			return this;
		}

		public Builder vulnerableDamage(float vulnerableDamage, HolderSet<EntityType<?>> vulnerableEntityTypes) {
			this.vulnerableDamage = vulnerableDamage;
			this.vulnerableEntityTypes = vulnerableEntityTypes;
			return this;
		}

		public Builder damageImmuneEntityTypes(HolderSet<EntityType<?>> damageImmuneEntityTypes) {
			this.damageImmuneEntityTypes = damageImmuneEntityTypes;
			return this;
		}

		public Builder spreadsFromZombieAttack(boolean spreadsFromZombieAttack) {
			this.spreadsFromZombieAttack = spreadsFromZombieAttack;
			return this;
		}

		public Builder spreadsFromIgniteEnchantments(boolean spreadsFromIgniteEnchantments) {
			this.spreadsFromIgniteEnchantments = spreadsFromIgniteEnchantments;
			return this;
		}

		public Builder replaceableByOtherFireTypes(boolean replaceableByOtherFireTypes) {
			this.replaceableByOtherFireTypes = replaceableByOtherFireTypes;
			return this;
		}

		public Builder alwaysApplyToEntityTypes(HolderSet<EntityType<?>> alwaysApplyToEntityTypes) {
			this.alwaysApplyToEntityTypes = alwaysApplyToEntityTypes;
			return this;
		}

		public Builder cannotApplyToEntityTypes(HolderSet<EntityType<?>> cannotApplyToEntityTypes) {
			this.cannotApplyToEntityTypes = cannotApplyToEntityTypes;
			return this;
		}

		public Builder textures(Identifier texture0, Identifier texture1) {
			this.texture0 = texture0;
			this.texture1 = texture1;
			return this;
		}

		public Builder enabledWhen(ConfigPredicate enabledWhen) {
			this.enabledWhen = enabledWhen;
			return this;
		}

		public FireType build() {
			return new FireType(
				new SourceSettings(
					this.fireSourceBlocks,
					this.supportingBlocks
				),
				new DamageSettings(
					this.damage,
					this.vulnerableDamage,
					this.vulnerableEntityTypes,
					this.damageImmuneEntityTypes
				),
				new SpreadSettings(
					this.spreadsFromZombieAttack,
					this.spreadsFromIgniteEnchantments,
					this.replaceableByOtherFireTypes,
					this.alwaysApplyToEntityTypes,
					this.cannotApplyToEntityTypes
				),
				new TextureSettings(
					Optional.ofNullable(this.texture0),
					Optional.ofNullable(this.texture1)
				),
				Optional.ofNullable(this.enabledWhen)
			);
		}

	}
}
