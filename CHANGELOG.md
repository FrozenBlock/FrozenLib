Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Added JavaDocs to classes that may be used frequently for ease of use outside FrozenBlock.
- Added a `disable` method to the `FrozenBiome` that prevents it from automatically being injected into worldgen.
- The `windoverride` command has been revamped and renamed to `wind.`
- Added the `EntityLootHelper` class, currently containing the `shouldSmeltLoot` method.
- Added the `TintRegistryHelper` class, making simple registry of block and item tints quicker.
- Significantly optimized how structure info is sent to the client in debug mode.
- Added the new `BeaconEffectRegistry` class, which can be used to add a Mob Effect to any Beacon tier.
- Added the new `SherdRegsistry` class, which can be used to easily implement new Pottery Sherds.
- Added the new `DecoratedPotPatternRegsitryEntrypoint` interface, which can be used to register new Decorated Pot Patterns.
  - The entrypoint's identitier is `frozenlib:decorated_pot_patterns`
- Fixed `FadingDiskCarpetFeature` using an incorrect position to determine if a block can survive.
- Added the `CapeRegistry` class which can be used to register custom capes.
  - Currently, these capes can only be accessed through FrozenLib's config.
  - An optional list of allowed UUIDs can be defined for capes.
- Fixed the `ColumnWithDiskFeature` not generating as intended.
- Added an API to send and receive files between the client and server.
  - This is useful in cases where, for example, you want to send a screenshot from Minecraft to the server to be used as a texture.
  - This can be disabled both client-side and server-side separately in FrozenLib's config.
- Significantly optimized DataFixing.
  - In cases where something like a structure with a pre-defined set of DataVersions is loaded and a separate mod with a DataFixer is loaded, the new DataFixer would run for every structure piece.
  - This would cause tremendous amounts of lag while not fixing any data, as no fixable data was present to begin with.
  - Due to this optimization, we recommend that any mods using FrozenLib implement a DataFixer, even an empty one, so it will have a 100% success rate of DataFixing.
- Added `IS_DATAGEN` to the `FrozenBools` class.
  - This is useful in cases you want to remove a BlockState Property during datagen so it doesn't create unnecessary data.
- `PlayerDamageSourceSounds` has been renamed to `PlayerDamageTypeSounds,` and now takes DamageType as a parameter instead of DamageSource.
- Added missing particle-related features to `BonemealBehaviors` in 1.21+.
