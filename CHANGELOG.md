Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed an issue with server textures to work as intended.
- Added `MarkForPostProcessingProcessor,` a structure processor that will mark matching blocks for post-processing.
- Added `DataMarkerProcessableLegacySinglePoolElement` and `DataMarkerProcessableSinglePoolElement,` jigsaw structure pool elements that does not remove Structure Blocks upon being placed.
  - Data Blocks can be processed by writing a mixin on `StructurePoolElement`'s `handleDataMarker` method.
- Fixed an issue that prevented Breezes from emitting a wind disturbance.
- Fixed an issue that allowed structures disabled via the `StructureGenerationConditionApi` to still cause lag when using the locate command.
