package net.frozenblock.lib.wind.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public interface WindManagerExtension {

	void tick();

	void baseTick();

	boolean runResetsIfNeeded();

	void createSyncByteBuf(FriendlyByteBuf original);

	void load(CompoundTag compoundTag);

	void save(CompoundTag compoundTag);
}
