Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed an issue with server textures to work as intended.
- Added `MarkForPostProcessingProcessor,` a structure processor that will mark matching blocks for post-processing.
- Added `DataMarkerProcessableLegacySinglePoolElement` and `DataMarkerProcessableSinglePoolElement,` jigsaw structure pool elements that do not remove Structure Blocks upon being placed.
  - Data Blocks can be processed by writing a mixin on `StructurePoolElement`'s `handleDataMarker` method.
- Fixed an issue that prevented Breezes from emitting a wind disturbance.
- Fixed an issue that allowed structures disabled via the `StructureGenerationConditionApi` to still cause lag when using the locate command.
- Added the `downloadResourcePack` and `downloadResourcePacks` methods to `FrozenLibModResourcePackApi`!
  - This method will download a resource pack/resource packs, which will be loaded either open Minecraft's next boot or resources being reloaded.
  - Resource Pack downloads can be "grouped" together using the `PackDownloadGroup` class.
  - The file the URL points to must be a JSON with the two following fields:
    - `pack`: The URL of the resource pack, which must be a `.zip` file.
    - `version`: A numerical id for the resource pack. If the pack has been downloaded before with the same `version`, the download will be skipped.
  - A toast will be displayed on-screen to tell the player when a pack has finished or failed downloading.
  - A config option to disable pack downloading and toasts has been added.
- Added the `CustomRotationalParticleRenderer` class, used to make the creation of particles with 3D rotation much easier.
  - An example of this can be seen in the `WindParticle` class.
- Removed two unused item textures.

### 1.21.5+
- Added `FrozenLibConfiguredTreeFeature` and `FrozenLibPlacedTreeFeature,` designed to speed up the process of designing tree variants both with and without leaf litters.

### 1.21.9+
- Removed FrozenLib's modified Vanilla debug renderers.
- Removed FrozenLib's modified Vanilla debug packets.
- Removed FrozenLib's `DebugRendererEvents` class.
- Removed FrozenLIb's custom Debug menu.
- FrozenLib's Wind and Wind Disturbance debug renderers are now enabled via the `MC_DEBUG_FROZENLIB_WIND` and `MC_DEBUG_FROZENLIB_WIND_DISTURBANCE` launch arguments.
