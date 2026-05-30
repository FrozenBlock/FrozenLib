package net.frozenblock.lib.block.client.impl.fire;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.fire.FireType;

@Environment(EnvType.CLIENT)
public interface LavaParticleFireTypeInterface {
	default void frozenLib$setFireType(FireType fireType) {
		throw new AssertionError();
	}
}
