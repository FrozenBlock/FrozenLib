package net.frozenblock.lib.platform.api.data;

import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface DataAttachmentSyncPredicate extends BiPredicate<Object, ServerPlayer> {

	static DataAttachmentSyncPredicate all() {
		return (holder, player) -> true;
	}

	static DataAttachmentSyncPredicate targetOnly() {
		return (holder, player) -> holder == player;
	}

	static DataAttachmentSyncPredicate allButTarget() {
		return (holder, player) -> holder != player;
	}
}
