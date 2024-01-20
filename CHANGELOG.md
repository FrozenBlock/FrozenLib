Hello
Put changelog in plain text please
Make sure to clear this after each release

Put changelog here:

-----------------
- Fixed Wind and some server-controlled Screen Shakes not working.
- Added a new /scale command (1.20.5+ only.)
- Added the `FrozenBiome` class, meant to make biome creation easier.
  - Contains an `injectToOverworld` method to directly add the biome to Overworld worldgen.
  - Be sure to create a public static final instance of your FrozenBiome in order to properly access and register it.
- Added the `getEntitiesPerLevel` method to `EntityUtils.`
  - This will return all loaded entities within a given `ServerLevel.`
- Added the `frozenlib:data_fix_types` entrypoint, using the class `FrozenMobCategoryEntrypoint.`
  - This is used for saved data, some Vanilla examples being Raids and Map data.
  - Custom Data Fix Types can be retrieved using the `getDataFixType` method in `FrozenDataFixTypes.`
- Improved the quality of the `AxeBehaviors` class.
