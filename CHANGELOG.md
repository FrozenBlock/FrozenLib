Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Added `FrozenLibModResourcePackApi`, allowing modders to include resource packs as .zip files with their mods.
  - This has only been tested with double-zipped resource packs, as the intent is to allow for the use of obfuscated resource packs.
  - Resource packs must be placed within a `frozenlib_resourcepacks` in the mod.
  - Resource packs will be extracted to the `frozenlib/resourcepacks` directory in Minecraft's run directory.
  - These resource packs will be force-enabled.
- FrozenLib's cape texture cache file path within Minecraft's run directory has been moved from `frozenlib_cape_cape` to `frozenlib/cape_cache`.
- Updated Kotlin to 2.2.0
