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

package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.storage.loot.providers.number.EnvironmentAttributeProvider;

public record EnvironmentAttributeValue(EnvironmentAttribute attribute) implements PitchProvider, EnvironmentAttributeProvider {
	private static final Codec<EnvironmentAttribute<?>> ATTRIBUTE_CODEC = EnvironmentAttributes.CODEC.validate(
		attribute -> attribute.type().toFloat() == null
			? DataResult.error(() -> attribute + " cannot be converted to a float")
			: DataResult.success(attribute)
	);
	public static final MapCodec<EnvironmentAttributeValue> MAP_CODEC = EnvironmentAttributeProvider.mapCodec(ATTRIBUTE_CODEC, EnvironmentAttributeValue::new);

	@Override
	public MapCodec<? extends PitchProvider> codec() {
		return MAP_CODEC;
	}

	@Override
	public float sample(Context context) {
		return attribute.type().toFloat(context.level().environmentAttributes().getValue(this.attribute, context.origin()));
	}
}
