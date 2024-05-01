Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Fixed the origin position of Entity-caused Wind Disturbances.
- Added XJS Configs
  - Serializes with [XJS](https://github.com/ExJson/xjs-data) to Djs, Json, Hjson, UbJson, and TXT files.
  - Special features supported: Typed entries, `@SaveToggle`, and `@Comment`
- Added `FrozenHeightmaps,` currently containing `MOTION_BLOCKING_NO_LEAVES_SYNCED,` a variant of Vanilla's `MOTION_BLOCKING_NO_LEAVES` that syncs with the client.
- Added `WolfVariantBiomeRegistry` to allow for modders to easily assign Wolf variants to new biomes.
  - If a biome has multiple variants registered, it will pick a random entry from the list of registered variants.
- Added the `RandomPoolAliasApi` to allow for modders to add new targets to structure aliases.
  - For example, adding new spawner types to Trial Chambers.
- Changed Typed Entry creation to `TypedEntry.create()`
  - `TypedEntry` is now an interface
- Fixed LGPL license being used in some places instead of GPL
- (1.20.5+) Updated minimum Java version to 21