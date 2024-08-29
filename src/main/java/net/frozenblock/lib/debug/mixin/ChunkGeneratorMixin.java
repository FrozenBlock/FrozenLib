package net.frozenblock.lib.debug.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

	@WrapWithCondition(
		method = "createReferences",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/protocol/game/DebugPackets;sendStructurePacket(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/StructureStart;)V"
		)
	)
	public boolean frozenLib$stopPacket(WorldGenLevel world, StructureStart structureStart) {
		return false;
	}

}
