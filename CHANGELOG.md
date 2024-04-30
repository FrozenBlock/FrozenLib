Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Fixed the origin position of Entity-caused Wind Disturbances.
- Added `FrozenHeightmaps,` currently containing `MOTION_BLOCKING_NO_LEAVES_SYNCED,` a variant of Vanilla's `MOTION_BLOCKING_NO_LEAVES` that syncs with the client.
- Added `WolfVariantBiomeRegistry` to allow for modders to easily assign Wolf variants to new biomes.
  - If a biome has multiple variants registered, it will pick a random entry from the list of registered variants.
- Added the `RandomPoolAliasApi` to allow for modders to add new targets to structure aliases.
  - For example, adding new spawner types to Trial Chambers.
- Added the `ColumnWithDiskScheduledTickFeature` feature.
  - Will only call scheduled ticks for blocks placed as part of the column.
