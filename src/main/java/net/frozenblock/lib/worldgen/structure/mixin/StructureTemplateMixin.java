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

package net.frozenblock.lib.worldgen.structure.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplateInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
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
			target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;getBoundingBox()Lnet/minecraft/world/level/levelgen/structure/BoundingBox;",
			shift = At.Shift.AFTER
		)
	)
	public synchronized void frozenLib$placeInWorld(
		ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, RandomSource random, int flags,
		CallbackInfoReturnable<Boolean> info
	) {
		this.frozenLib$additionalProcessors.forEach(settings::addProcessor);
		this.frozenLib$additionalProcessors.clear();
	}

	@Override
	public synchronized void frozenLib$addProcessors(List<StructureProcessor> processors) {
		this.frozenLib$additionalProcessors.addAll(processors);
	}

	@WrapOperation(
		method = "placeInWorld",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
			ordinal = 1
		)
	)
	private static boolean frozenLib$scheduleTicksOnBlockPlace(ServerLevelAccessor instance, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
		final boolean setBlock = original.call(instance, pos, state, i);
		if (setBlock && state.is(FrozenBlockTags.STRUCTURE_PLACE_SCHEDULES_TICK)) instance.scheduleTick(pos, state.getBlock(), 1);
		return setBlock;
	}
}
