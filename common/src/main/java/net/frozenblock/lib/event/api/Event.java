package net.frozenblock.lib.event.api;

/**
 * A cross-platform event, analogous to Fabric's {@code Event<T>}.
 *
 * <p>On Fabric this is backed directly by Fabric's array-backed event system.
 * On NeoForge this is backed by a real {@code IEventBus} dispatch.
 *
 * @param <T> the listener callback type
 */
public interface Event<T> {
	T invoker();
	void register(T callback);
	void unregister(T callback);
	boolean isRegistered(T callback);
	void clearCallbacks();
}
