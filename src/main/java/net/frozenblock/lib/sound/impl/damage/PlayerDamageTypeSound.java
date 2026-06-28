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
