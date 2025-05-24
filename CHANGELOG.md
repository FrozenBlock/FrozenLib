Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Removed a feature that caused Netty to throw exceptions instead of holding them back.
  - While with the vast majority of mods this behavior was fine, some mods interact with these features without using try/catch statements to avoid this situation after finding the cause.
  - Hence, this was removed. For better or for worse.
- Removed `StructurePoolElementIdReplacements` after being deprecated and not having functionality for a while.
- Removed all parameter-related fields from `OverworldBiomeBuilderParameters`, as the other individual parameter classes contain these.
- Refactored `BiomeParameters` to the `impl` package, from the `api` package.
- `NoisePlacementFilter` now takes `EasyNoiseSampler.NoiseType` as a parameter to determine which type of noise to use instead of an Integer.
- Removed the second type parameter from `FrozenLibConfiguredFeature`, making its usage in datagen significantly easier to work with.
- Cleaned up multiple classes.
