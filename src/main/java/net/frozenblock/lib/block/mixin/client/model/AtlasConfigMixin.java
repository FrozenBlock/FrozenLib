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

package net.frozenblock.lib.block.mixin.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.resources.metadata.emissive.EmissiveMetadataSection;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(AtlasManager.AtlasConfig.class)
public class AtlasConfigMixin {

	@Mutable
	@Shadow
	@Final
	Set<MetadataSectionType<?>> additionalMetadata;

	@Inject(
		method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLjava/util/Set;)V",
		at = @At("TAIL")
	)
	private void frozenLib$addEmissiveMetadataSection(CallbackInfo info) {
		ArrayList<MetadataSectionType<?>> sections = new ArrayList<>(this.additionalMetadata);
		sections.add(EmissiveMetadataSection.TYPE);
		this.additionalMetadata = Set.copyOf(sections);
	}

}
