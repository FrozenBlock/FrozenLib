package net.frozenblock.lib.config.api.sync;

/**
 * Used to provide the behavior for a config entry when syncing is in action.
 * See {@link net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig} for an example.
 * @since 1.5
 */
public enum SyncBehavior {

	/**
	 * The default behavior for all entries.
	 * Operators of the server and LAN hosts will be able to set the config values, and all others will see that value update on their client.
	 * Non-operator/host clients will not be able to modify these values, as they will be locked in the GUI (currently we provide native support for Cloth Config only,) and display the reason why it can't be modified.
	 * The values synced to the client will not save to their config, as this is simply a temporary modification.
	 */
	SYNCABLE(true),

	/**
	 * The config entry will not sync, and will be modifiable to anyone's client.
	 * This should be used for client-only options, for instance, whether or not you want a specific particle to spawn.
	 */
	UNSYNCABLE(false),

	/**
	 * The config entry will be locked whilst connected to the server or LAN world, but will respect the client's value as it will not sync.
	 * Anyone will be able to leave, change the value, then rejoin and have their own option enabled.
	 * This is recommended to be used for client-only options that may cause bugs or other issues if changed during gameplay.
	 * Keep in mind that operators/hosts will be able to modify this entry at any time for themselves.
	 */
	LOCK_WHEN_SYNCED(false);

	private final boolean canSync;

	private SyncBehavior(boolean canSync) {
		this.canSync = canSync;
	}

	public boolean canSync() {
		return this.canSync;
	}
}
