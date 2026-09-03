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

package net.frozenblock.lib.levelgen.surface.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Holds both a {@link Identifier} and {@link SurfaceRules.RuleSource}.
 * The Identifier denotes the dimension to be modified, and the RuleSource are the rules to be applied to it.
 */
public record DimensionBoundRuleSource(Identifier dimension, SurfaceRules.RuleSource ruleSource) {
	public static final Codec<DimensionBoundRuleSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Identifier.CODEC.fieldOf("dimension").forGetter(DimensionBoundRuleSource::dimension),
		SurfaceRules.RuleSource.CODEC.fieldOf("rule_source").forGetter(DimensionBoundRuleSource::ruleSource)
	).apply(instance, DimensionBoundRuleSource::new));

	// todo more compact stream codec
	public static final StreamCodec<ByteBuf, DimensionBoundRuleSource> STREAM_CODEC = StreamCodec.composite(
		Identifier.STREAM_CODEC,
		DimensionBoundRuleSource::dimension,
		ByteBufCodecs.fromCodec(SurfaceRules.RuleSource.CODEC),
		DimensionBoundRuleSource::ruleSource,
		DimensionBoundRuleSource::new
	);
}
