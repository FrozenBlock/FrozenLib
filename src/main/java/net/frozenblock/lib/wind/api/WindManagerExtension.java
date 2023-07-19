package net.frozenblock.lib.wind.api;

import net.minecraft.network.FriendlyByteBuf;

public interface WindManagerExtension {

	void tick(WindManager manager);

	void clientTick();

	void baseTick();

	boolean runResetsIfNeeded(WindManager manager);

	FriendlyByteBuf createSyncByteBuf(WindManager manager, FriendlyByteBuf original);
}
