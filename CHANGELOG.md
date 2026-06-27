Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Fixed a crash that could occur on clients when trying to sync Wind Manager Extensions with the server.
- Wind Manager Extensions now support modification as a method of syncing, instead of replacement.
- Fixed an issue that could cause the number of Wind Manager Extensions on the client to increase dramatically over time.
- `DataMarkerProcessableSinglePoolElement` and `DataMarkerProcessableLegacySinglePoolElement` are now both abstract classes.
  - This change allows for easy handling of Data Markers without the need for mixins.
- Added the `StructureSetApi` class, allowing modders to easily add new `Structure`s to `StructureSet`s.
- Added `RuleSourceAddition`s, replacing the previous API used to add new `RuleSource`s to Levels.
  - Added the `frozenlib:rule_source_addition` Dynamic Registry, with the following format:
    - `dimensions`: A Dimension Type's ID, a list of Dimension Type IDs, or a Dimenion Type Tag that `rule_sources` will be added to.
    - `has_preliminary_surface`: Whether `rule_sources` can only generate on the world's surface. (i.e., the surface of the Overworld.)
    - `rule_source`: The `RuleSource` to add to `dimensions`.
  - Removed `SurfaceRuleEvents`, `DimensionBoundRuleSource`, and `SurfaceRuleUtil` as they are no longer needed.
- Added the `#frozenlib:overworld`, `#frozenlib:nether`, and `#frozenlib:end` Dimension Type Tags.
