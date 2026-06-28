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

package net.frozenblock.lib.sound.impl.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;

public record PlayerDamageTypeSound(HolderSet<DamageType> damageTypes, Holder<SoundEvent> sound, Optional<ConfigPredicate> enabledWhen) {
	public static final Codec<PlayerDamageTypeSound> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).fieldOf("damage_types").forGetter(PlayerDamageTypeSound::damageTypes),
		SoundEvent.CODEC.fieldOf("sound_event").forGetter(PlayerDamageTypeSound::sound),
		ConfigPredicate.CODEC.optionalFieldOf("config_predicate").forGetter(PlayerDamageTypeSound::enabledWhen)
	).apply(instance, PlayerDamageTypeSound::new));

	public boolean enabledAndMatches(Holder<DamageType> damageType) {
		return this.isEnabled() && this.matches(damageType);
	}

	public boolean matches(Holder<DamageType> damageType) {
		return this.damageTypes.contains(damageType);
	}

	public boolean isEnabled() {
		return this.enabledWhen.map(ConfigPredicate::test).orElse(true);
	}
}
