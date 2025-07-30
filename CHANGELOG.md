Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed an issue with server textures to work as intended.
- Added `MarkForPostProcessingProcessor,` a structure processor that will mark matching blocks for post-processing.
- Added `DataMarkerProcessableLegacySinglePoolElement` and `DataMarkerProcessableSinglePoolElement,` jigsaw structure pool elements that does not remove Structure Blocks upon being placed.
  - Data Blocks can be processed by writing a mixin on `StructurePoolElement`'s `handleDataMarker` method.
- Fixed an issue that prevented Breezes from emitting a wind disturbance.
- Fixed an issue that allowed structures disabled via the `StructureGenerationConditionApi` to still cause lag when using the locate command.
- Added the `downloadResourcePack` method to `FrozenLibModResourcePackApi`!
  - This method will download a resource pack, which will be loaded either open Minecraft's next boot or resources being reloaded.
  - The file the URL points to must be a JSON with the two following fields:
    - `pack`: The URL of the resource pack, which must be a `.zip` file.
    - `version`: A numerical id for the resource pack. If the pack has been downloaded before with the same `version`, the download will be skipped.
  - A toast will be displayed on-screen to tell the player when a pack has finished or failed downloading.
  - Config options to disable pack downloading and toasts have been added.
