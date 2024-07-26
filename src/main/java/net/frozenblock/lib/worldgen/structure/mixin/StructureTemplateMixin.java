/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.structure.mixin;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplateInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin implements StructureTemplateInterface {

	@Unique
	private final List<StructureProcessor> frozenLib$additionalProcessors = new ArrayList<>();

	@Inject(
		method = "placeInWorld",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;getRandomPalette(Ljava/util/List;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$Palette;"
		)
	)
	public void frozenLib$placeInWorld(
		ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, RandomSource random, int flags,
		CallbackInfoReturnable<Boolean> info
	) {
		this.frozenLib$additionalProcessors.forEach(settings::addProcessor);
	}

	@Override
	public void frozenLib$addProcessors(List<StructureProcessor> processors) {
		this.frozenLib$additionalProcessors.addAll(processors);
	}
}
