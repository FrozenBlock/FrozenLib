Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed an issue with server textures to work as intended.
- Added `MarkForPostProcessingProcessor,` a structure processor that will mark matching blocks for post-processing.
- Added `DataMarkerProcessableLegacySinglePoolElement,` a jigsaw structure pool element that does not remove Structure Blocks upon being placed.
  - Data Blocks can be processed by writing a mixin on `StructurePoolElement`'s `handleDataMarker` method.
