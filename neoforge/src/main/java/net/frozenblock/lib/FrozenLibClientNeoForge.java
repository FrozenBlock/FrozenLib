package net.frozenblock.lib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = FrozenLibConstants.MOD_ID, dist = Dist.CLIENT)
public final class FrozenLibClientNeoForge {

	public FrozenLibClientNeoForge(IEventBus eventBus) {
		FrozenLibClient.init();
	}
}
