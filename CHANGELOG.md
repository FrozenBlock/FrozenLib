Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Updated embedded Jankson
  - Adds `SaveToggle` annotation
- Moved all field annotations for config syncing to `EntrySyncData` for easier use
  - Fixed a small bug with `Locked When Synced` fields syncing instead of remaining stagnant
  - Added an additional tooltip to notify Server Operators and LAN Hosts when a config value will sync
- Moved most packets from channels to `FabricPacket`s
