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

package net.frozenblock.lib.item.impl.component.consume_effects;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.item.api.component.consume_effects.DamageConsumeEffect;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

@UtilityClass
public final class FrozenLibConsumeEffects {
	private static final DeferredRegister<ConsumeEffect.Type<?>> REGISTER = DeferredRegister.create(
		Registries.CONSUME_EFFECT_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<DamageConsumeEffect>> DAMAGE = register(
		"damage",
		DamageConsumeEffect.CODEC,
		DamageConsumeEffect.STREAM_CODEC
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <T extends ConsumeEffect> DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<T>> register(
		String name,
		MapCodec<T> codec,
		StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
	) {
		return REGISTER.register(name, () -> new ConsumeEffect.Type<>(codec, streamCodec));
	}
}
