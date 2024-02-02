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
- Upgraded Gravity API to use directional gravity using `Vec3`s
- Added the `getEntitiesPerLevel` method to `EntityUtils`
  - This will return all loaded entities within a given `ServerLevel.`
- Improved the quality of the `AxeBehaviors` class.
- Added an `extensionID` method to the `WindManagerExtension` class in order to improve saving with NBT.
- Moved `BlockShecuduledTicks` to the `block` package.
- `PointedDripstoneBlockMixin` is now loaded once again.
- Many other improvements
