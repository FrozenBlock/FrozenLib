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

package net.frozenblock.lib.sound.api.damage;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.sound.impl.damage.PlayerDamageTypeSound;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class PlayerDamageTypeSounds {

	@ApiStatus.Internal
	public static SoundEvent getSoundForTypeOr(RegistryAccess registryAccess, Holder<DamageType> damageType, SoundEvent original) {
		return registryAccess.lookup(FrozenLibRegistries.PLAYER_DAMAGE_TYPE_SOUND)
			.map(registry -> registry.stream()
				.filter(playerDamageTypeSound -> playerDamageTypeSound.enabledAndMatches(damageType))
				.findFirst()
				.map(playerDamageTypeSound -> playerDamageTypeSound.sound().value())
				.orElse(original)
			).orElse(original);
	}

	@ApiStatus.Internal
	public static SoundEvent getSoundForTypeOr(RegistryAccess registryAccess, DamageType damageType, SoundEvent original) {
		return getSoundForTypeOr(registryAccess, registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).wrapAsHolder(damageType), original);
	}

	public static ResourceKey<PlayerDamageTypeSound> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.PLAYER_DAMAGE_TYPE_SOUND, id);
	}

	public static void register(
		BootstrapContext<PlayerDamageTypeSound> context,
		ResourceKey<PlayerDamageTypeSound> key,
		HolderSet<DamageType> damageTypes,
		Holder<SoundEvent> soundEvent
	) {
		context.register(key, new PlayerDamageTypeSound(damageTypes, soundEvent, Optional.empty()));
	}

	public static void register(
		BootstrapContext<PlayerDamageTypeSound> context,
		Identifier id,
		HolderSet<DamageType> damageTypes,
		Holder<SoundEvent> soundEvent
	) {
		register(context, createKey(id), damageTypes, soundEvent);
	}

	public static void register(
		BootstrapContext<PlayerDamageTypeSound> context,
		ResourceKey<PlayerDamageTypeSound> key,
		HolderSet<DamageType> damageTypes,
		Holder<SoundEvent> soundEvent,
		ConfigPredicate enabledWhen
	) {
		context.register(key, new PlayerDamageTypeSound(damageTypes, soundEvent, Optional.of(enabledWhen)));
	}

	public static void register(
		BootstrapContext<PlayerDamageTypeSound> context,
		Identifier id,
		HolderSet<DamageType> damageTypes,
		Holder<SoundEvent> soundEvent,
		ConfigPredicate enabledWhen
	) {
		register(context, createKey(id), damageTypes, soundEvent, enabledWhen);
	}
}
