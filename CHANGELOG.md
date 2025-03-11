Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Revamped the Block Sound Type API entirely, fixing a few glaring issues.
  - Block sound type overrides now use a `HolderSet` to define which blocks are affected.
    - This allows tags and lists to work properly with this system, saving space in RAM.
    - The new field for blocks is named `blocks`.
  - The data-driven directory for block sound type overrides has been changed from `blocksoundoverwrites` to `block_sound_overwrites`.
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
- Removed many duplicate worldgen feature implementations, now just relying on HolderSets.
- Cleaned up existing worldgen feature configurations and features.
- Disconnecting from a FrozenLib server and joining a non-FrozenLib server afterwards no longer leaves client Wind running.
- Wind can no longer return any non-zero value when on a non-FrozenLib server without the config option to override this behavior enabled.
- Significantly cleaned up the Dripstone API and related mixins, removing edits that cause compatability issues in the process.
- Added `StructurePlacementExclusionApi,` which lets you add exclusion zones to existing structures.
- Added `BlockRandomTicks,` allowing modders to add new Random Ticks to any existing block with ease.
- `BlockScheduledTick` behavior now runs prior to Vanilla scheduled ticks, and runs for all blocks instead of only those without defined Scheduled Ticks.
- Added `StructureGenerationConditionApi,` allowing modders to define custom conditions under which structures can generate with a Boolean Supplier.
- Completely rewrote the Noise Path worldgen feature, being much more extensive and configurable.
  - This has resulted in two other features being removed, as their behavior is now possible to achieve with the base feature.
- Replaced the Upwards and Downwards column worldgen features with a single column feature, with a more robust config.
- Optimized entity easter egg texture rendering.
- Added `UnderWaterVegetationPatchFeature.`
