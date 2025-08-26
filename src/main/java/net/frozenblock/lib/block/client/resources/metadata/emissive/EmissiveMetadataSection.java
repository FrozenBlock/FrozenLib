/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.block.client.resources.metadata.emissive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ExtraCodecs;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public record EmissiveMetadataSection(int lightEmission, Optional<Boolean> shade) {
	public static final Codec<EmissiveMetadataSection> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				ExtraCodecs.intRange(0, 15).optionalFieldOf("frametime", 15).forGetter(EmissiveMetadataSection::lightEmission),
				Codec.BOOL.optionalFieldOf("shade").forGetter(EmissiveMetadataSection::shade)
			)
			.apply(instance, EmissiveMetadataSection::new)
	);
	public static final MetadataSectionType<EmissiveMetadataSection> TYPE = new MetadataSectionType<>("frozenlib_emissive", CODEC);
}
