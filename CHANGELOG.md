Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed a crash that could occur on clients when trying to sync Wind Manager Extensions with the server.
- Wind Manager Extensions now support modification as a method of syncing, instead of replacement.
- Fixed an issue that could cause the number of Wind Manager Extensions on the client to increase dramatically over time.
- `DataMarkerProcessableSinglePoolElement` and `DataMarkerProcessableLegacySinglePoolElement` are now both abstract classes.
  - This change allows for easy handling of Data Markers without the need for mixins.
- Added the `StructureSetApi` class, allowing modders to easily add new `Structure`s to `StructureSet`s.
