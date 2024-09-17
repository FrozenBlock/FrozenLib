Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
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
