Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Revamped the Block Sound Type API entirely, fixing a few glaring issues.
  - Code-defined overwrites can now define:
    - Individual Blocks.
    - Arrays of Blocks.
    - Lists of ResourceLocations.
    - Block Tags.
  - Data-drive overwrites can now define:
    - Lists of ResourceLocations.
    - Block Tags.
  - Overwrites that list multiple blocks no longer create one instance per-block, saving space in RAM.
  - Block Tags now actually work in overwrites.
- All Fading Disk features check if surrounding blocks are replaceable, instead of strictly Air.
- Potentially fixed an issue with C2ME during structure generation.
- `BlockStateRespectingProcessorRule` now maintains water if a waterlogged block is replaced with air.
- Added tinted flower bed models.
- Cleaned up the Screen Shake system and made it much smoother.
- Cleaned up classes in the `block` package and condensed some of its contents.
- Cleaned up classes in the `entity` package and condensed the `modcategory` package into it.
- Renamed `FrozenFeatureFlags` to `FeatureFlagAPI.`
- Reworked, renamed, and cleaned up the Axe, Bone Meal, and Shovel item use APIs.
- Cleaned up classes in the `sound` package and condensed some of its contents.
- Flyby sounds can now be properly sent to the client.
- Combined the `storage` and `block` packages.
- Removed a few unnecessary APIs.
- Added the `requires_air_or_water_in_area_noise_path_feature,` which only places blocks when Air or Water is within a certain distance.
